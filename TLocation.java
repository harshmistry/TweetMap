package com.nyu.tweetmap;

public class TLocation
{
	private double latitude,longitude;

	public TLocation(String latitude, String longitude) 
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
