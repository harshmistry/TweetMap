package com.nyu.tweetmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;


public class DynamoDataRetrieval 
{
	public static AmazonDynamoDB dynamodb;
	BasicAWSCredentials credentials;
	private static String TABLE_NAME="tweetslocation";
	private static String ATTRIBUTE_UUID="uuid";
	private static String ATTRIBUTE_LAT="latitude";
	private static String ATTRIBUTE_LNG="longitude";
	private static String ATTRIBUTE_SEARCH_KEY="keyword";


	public DynamoDataRetrieval()
	{
		credentials=new BasicAWSCredentials(SERCRE_KEY, SECRET_KEY);
		dynamodb = new AmazonDynamoDBClient(credentials);
		Region uswest2= Region.getRegion(Regions.US_WEST_2);
		dynamodb.setRegion(uswest2);
		System.out.println("I am in Dynamo");
	}

	public List<TLocation> getItems(String search_Key) 
	{
		
		HashMap<String,Condition> scanFilter= new HashMap<String,Condition>();
			Condition condition= new Condition()
			.withComparisonOperator(ComparisonOperator.EQ.toString())
			.withAttributeValueList(new AttributeValue().withS(search_Key));
			scanFilter.put(ATTRIBUTE_SEARCH_KEY, condition);
		
		ScanRequest scanRequest= new ScanRequest(TABLE_NAME).withScanFilter(scanFilter);
		ScanResult scanResult = dynamodb.scan(scanRequest);
		
		List<Map<String,AttributeValue>> myresult = scanResult.getItems();
		List<TLocation> locationData= new ArrayList<TLocation>();
		
		System.out.println(search_Key);
		System.out.println(myresult.size());
		
		for(int i=0;i<myresult.size();i++){
			locationData.add(new TLocation(myresult.get(i).get(ATTRIBUTE_LAT).getS().toString(), myresult.get(i).get(ATTRIBUTE_LNG).getS().toString()));
		}
		
		return locationData;
	}

}

