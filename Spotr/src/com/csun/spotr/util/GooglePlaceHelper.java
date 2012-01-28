package com.csun.spotr.util;

import android.location.Location;

public class GooglePlaceHelper {
	public static final String GOOGLE_PLACE_API_KEY = "AIzaSyB3LpeNgrKCPBbYovm9smzQ-7dfiIb8nnI";
	public static final String GOOGLE_RADIUS_IN_METER = "100";
	public static final String RADIUS_IN_KM = "0.1";
	
	public static String buildGooglePlacesUrl(Location location, String radius) {
		StringBuilder url = new StringBuilder();
		url.append("https://maps.googleapis.com/maps/api/place/search/json?location=");
		url.append(Double.toString(location.getLatitude()));
		url.append(",");
		url.append(Double.toString(location.getLongitude()));
		url.append("&radius=");
		url.append(radius);
		url.append("&sensor=true&key=");
		url.append(GOOGLE_PLACE_API_KEY);
		return url.toString();
	}
	
	public static String buildGooglePlaceDetailsUrl(String reference) {
		StringBuilder url = new StringBuilder();
		url.append("https://maps.googleapis.com/maps/api/place/details/json?reference=");
		url.append(reference);
		url.append("&sensor=true&key=");
		url.append(GOOGLE_PLACE_API_KEY);
		return url.toString();
	}
}
