package com.nyu.tweetmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class UpdateTweets implements Runnable {

	//static AWSCredentials credentials = null;
	static BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAIX3ROOTJSNMUPZQA", "TtY1/u3o5QInvvQHBS8ESMWAof/RdTguGmq8tSPw");
	private static final String KEY_UUID="uuid";
	private static final String KEY_KEYWORD="keyword";
	private static final String KEY_LATITUDE="latitude";
	private static final String KEY_LONGITUDE="longitude";
	final static String tableName = "tweetslocation";
	private static AmazonDynamoDBClient dynamoDB;
	private static DynamoDB db;
	private static ConfigurationBuilder cb;
	
	private TwitterStream twitterStream;
	final String[] keywordsArray = { "#halloween","job","sport","restaurant","usa","#halloween","USA","thanksgiving","music"};
    static ArrayList<TweetsData> tweetList;
	
	@Override
	public void run() {
		System.out.println("Background process started");	
		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

		StatusListener listener = new StatusListener() {

			private int totalCount=0;

			@Override
			public void onException(Exception arg0) {
				System.out.println("onEXception" + arg0.getMessage()+"*******************");

			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				//System.out.println("onTrackLimitation");
			}

			@Override
			public void onStatus(Status status) {
			
				try {
		
					if(status.getGeoLocation()!=null)
					{
						++totalCount;
						System.out.println("Total Tweets Received : "+totalCount);
						for(String keyword : keywordsArray)
						{
							if(status.getText().contains(keyword))
							{
								tweetList.add(new TweetsData(UUID.randomUUID().toString(), keyword, status.getGeoLocation().getLatitude(), status.getGeoLocation().getLongitude()));
								break;
							}
							
						}
						
						
						if(totalCount==100)
						{
							insertDatatoDynamo(tweetList);
							totalCount=0;
							
						}
					}
				}
						
				 catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			}

			@Override
			public void onStallWarning(StallWarning stat) {
				System.out.println("onStallWarning");
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				System.out.println("onScrubGeo");

			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice arg0) {
				// TODO Auto-generated method stub

			}
		};

		twitterStream.addListener(listener);
		twitterStream.filter(new FilterQuery(keywordsArray));


	}

	protected void insertDatatoDynamo(ArrayList<TweetsData> tweetsCollection) 
	{
		PutItemRequest putItemRequest;
		PutItemResult putItemResult;
		System.out.println("Size of Tweets Data : "+tweetsCollection.size());
		for(TweetsData data : tweetsCollection)
		{
			Map<String,AttributeValue> item = newItem(data);
			putItemRequest = new PutItemRequest(tableName, item);
			putItemResult = dynamoDB.putItem(putItemRequest);
			System.out.println("Result : "+putItemResult);
		}
		
		
		
		tweetList.clear();
	}

	private Map<String, AttributeValue> newItem(TweetsData data) {
		Map<String,AttributeValue> item = new HashMap<String,AttributeValue>();
		item.put(KEY_UUID, new  AttributeValue(data.uuid));
		item.put(KEY_KEYWORD, new  AttributeValue(data.keyword));
		item.put(KEY_LATITUDE, new  AttributeValue(data.latitude.toString()));
		item.put(KEY_LONGITUDE, new  AttributeValue(data.longitude.toString()));
		
		return item;
	}

	public static void init() throws Exception {
		
		setUpTwitter();
		setUpDynamo();
		tweetList= new ArrayList<TweetsData>();
		
	}

	private static void setUpDynamo() throws Exception {
		
		/*try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
		                    "Please make sure that your credentials file is at the correct " +
		                    "location (/Users/harshmistry/.aws/credentials), and is in valid format.",
		                    e);
		}*/
		
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		dynamoDB.setRegion(usWest2);
		db = new DynamoDB(dynamoDB);
		System.out.println("First Init Success");
	
		try {
			
			// Create table if it does not exist yet
			
			if (Tables.doesTableExist(dynamoDB, tableName)) {
				System.out.println("Table " + tableName + " is already ACTIVE");
			} else {
			
				// Create a table with a primary hash key named 'name', which holds a string
			
				CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
						.withKeySchema(new KeySchemaElement().withAttributeName("uuid").withKeyType(KeyType.HASH))
						.withAttributeDefinitions(new AttributeDefinition().withAttributeName("uuid").withAttributeType(ScalarAttributeType.S))
						.withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));
				
				TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest).getTableDescription();
				System.out.println("Created Table: " + createdTableDescription);

				// Wait for it to become active
				
				System.out.println("Waiting for " + tableName + " to become ACTIVE...");
				Tables.awaitTableToBecomeActive(dynamoDB, tableName);
			}

			// Describe our new table
			
			DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
			TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
			System.out.println("Table Description: " + tableDescription);
		} catch (AmazonServiceException ase) {
			
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}


		
		
	}

	private static void setUpTwitter() {
		
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("oaDxyWPbtMR4onc5DrLXe63xA")
		.setOAuthConsumerSecret("ANtXjlaKko0H5rzhunUpa5lPJZZ1u63ziXmc50hsMv4tBdqIJ1")
		.setOAuthAccessToken("2771061159-ovHIYssc2kMJ4ntTCi1MtcuciaNG3MgLi2RBGyI")
		.setOAuthAccessTokenSecret("dsyjMTOH89M4QYsUJlfHnoUaWeFfYeLnmJWRFquMwSCZ2");
		
	}

	class TweetsData
	{
		String uuid;
		String keyword;
		Double latitude;
		Double longitude;
		
		public TweetsData(String uuid, String keyword, Double latitude,Double longitude) 
		{
			this.uuid = uuid;
			this.keyword = keyword;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
	
				
	}
	
}
