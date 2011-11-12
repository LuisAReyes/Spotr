package com.csun.spotr.helper;

import android.location.Location;
/**
 * @author Chan Nguyen
 */
public class GooglePlaceHelper {
	static final String GOOGLE_PLACE_API_KEY = "AIzaSyB3LpeNgrKCPBbYovm9smzQ-7dfiIb8nnI";
	public static String buildGooglePlaceUrl(Location location, String radius) {
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
}
