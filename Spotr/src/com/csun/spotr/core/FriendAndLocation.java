package com.csun.spotr.core;

public class FriendAndLocation {
    private final int userId;
    private final String name;
    private final double latitude;
    private final double longitude;
    private final String time;
    
    public FriendAndLocation(int userId, String name, double latitude, double longitude, String time) {
	this.userId = userId;
	this.name = name;
	this.latitude = latitude;
	this.longitude = longitude;
	this.time = time;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    
    public String getTime() {
	return time;
    }
}
