package com.nyu.tweetmap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class TweetFetcher {

	static AmazonDynamoDBClient dynamoDB;
	static DynamoDB db;
	public static int count=0;
	public static int totalCount=0;
	static File file;
	static 	TableWriteItems twi;
	static String skey="",statusT="";
	
	
	private static void init() throws Exception {

		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (C:\\Users\\Swar\\.aws\\credentials).
		 */
		
		AWSCredentials credentials = null;
		
		try {
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (C:\\Users\\USER_NAME\\.aws\\credentials), and is in valid format.",e);
		}
		
		dynamoDB = new AmazonDynamoDBClient(credentials);
		Region usWest2 = Region.getRegion(Regions.US_WEST_2);
		dynamoDB.setRegion(usWest2);
		db = new DynamoDB(dynamoDB);
		
	}

	public static void main(String[] args) throws TwitterException, IOException,Exception  {

		init();
		System.out.println("I am in Twitter Fetcher");
		
		final String tableName = "tweetslocation";
		PutRequest PutReq = new  PutRequest ();
		WriteRequest WriteReq = new  WriteRequest ();
		List <WriteRequest> ItemList = new  ArrayList <WriteRequest> ();
		
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

		final TwitterStream twitterStream;
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(YOUR_CONSUMER_KEY)
		.setOAuthConsumerSecret(YOUR_CONSUMER_SECRET)
		.setOAuthAccessToken(YOUR_ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(YOUR_ACCESS_TOKEN_SECRET);
		
//		File tweets = new File("tweets.txt");
//		file= new File("tweets.txt");
//		final BufferedWriter bw= new BufferedWriter(new FileWriter(file));
		
	    final String[] keywordsArray = { "sport","music", "game","mobile","food", "restaurant ","#halloween","us","#thanks","worldcup" ,"job"};
	    FilterQuery filtr = new FilterQuery(keywordsArray);
	    filtr.track(keywordsArray);
	    

		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

		StatusListener listener = new StatusListener() {

			@Override
			public void onException(Exception arg0) {
				System.out.println("onEXception" + arg0.getMessage()+"*******************");

			}

			@Override
			public void onTrackLimitationNotice(int arg0) {
				//System.out.println("onTrackLimitation");
			}

			@Override
			public void onStatus(Status stat) {
				
				// TODO Auto-generated method stub
				totalCount++;

				try {
		
					if(stat.getGeoLocation()!=null)
					{
						count++;
//						bw.write("{lat:"+stat.getGeoLocation().getLatitude() + ", lng:"+stat.getGeoLocation().getLongitude()+"}");
//						bw.write("#");
						skey="";
						statusT=stat.getText();
						System.out.println(count);
						for(int p=0;p<keywordsArray.length;p++)
						{
							if(statusT.toLowerCase().contains(keywordsArray[p]))
							{
									skey = keywordsArray[p];
									break;
							}
							else
							{
								skey="default";
							}
						}
						
						Map<String, AttributeValue> item = newItem(UUID.randomUUID().toString(), skey, Double.toString(stat.getGeoLocation().getLatitude()),Double.toString(stat.getGeoLocation().getLongitude()));
						
												PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
									            PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
					}


					if(count==5000)
					{	
					
//				            Map <String, List <WriteRequest >> requestItems = new  HashMap <String, List <WriteRequest >> ();
//				            requestItems.put (tableName, ItemList);
				        
//						bw.close();
						twitterStream.cleanUp();
						
						// scan all items

						ScanRequest scanRequest = new ScanRequest()
						.withTableName(tableName);

						ScanResult result = dynamoDB.scan(scanRequest);
						//				for (Map<String, AttributeValue> item2 : result.getItems()){
						//				   System.out.println(item2.get("name").toString());
						//				}
						
						List<Map<String,AttributeValue>> myresult = result.getItems();
						List<Location> locationData= new ArrayList<Location>();
						for(int i=0;i<myresult.size();i++){
							locationData.add(new Location(myresult.get(i).get("lat").getS().toString(), myresult.get(i).get("lng").getS().toString()));
						}
						
					}


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.print(".");
				//				System.out.println("Tweets Read "+totalCount);
			
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
		
		 twitterStream.firehose(50);
		 twitterStream.filter(filtr);
//		twitterStream.sample();
	
	}

	private static Map<String, AttributeValue> newItem(String uuid, String searchkey, String lat,String lng) {
	
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		
		item.put("uuid", new AttributeValue(uuid));
		item.put("searchkey", new AttributeValue(searchkey));
		item.put("lat", new AttributeValue(lat));
		item.put("lng", new AttributeValue(lng));

		return item;
	}
}

class Location{
	private double latitude,longitude;

	public Location(String latitude, String longitude) 
	{
		this.latitude= Double.parseDouble(latitude);
		this.longitude= Double.parseDouble(longitude);

	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
