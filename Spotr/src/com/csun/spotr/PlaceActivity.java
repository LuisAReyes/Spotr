package com.csun.spotr;


import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;

import android.content.Context;
import android.graphics.Bitmap;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.helper.DownloadImageHelper;
import com.csun.spotr.helper.GooglePlaceHelper;
import com.csun.spotr.helper.JsonHelper;
import com.csun.spotr.gui.LocationItemAdapter;

/**
 * @author: Chan Nguyen
 */
public class PlaceActivity extends Activity {
	private ListView locationsListView;
	private LocationItemAdapter locationItem;

	private Location currentLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set up layout
		setContentView(R.layout.place);
		currentLocation = getPhoneLocation();
		process(currentLocation);
	}

	public void process(Location location) {
		String url = GooglePlaceHelper.buildGooglePlaceUrl(location, "1000");
		JSONObject json = JsonHelper.getJSONfromURL(url);

		String[] names = null;
		String[] categories = null;
		String[] ratings = null;
		Bitmap[] iconBitmaps = null;
		try {
			JSONArray placeInformationArray = json.getJSONArray("results");
			
			names = new String[placeInformationArray.length()];
			categories = new String[placeInformationArray.length()];
			ratings = new String[placeInformationArray.length()];
			iconBitmaps = new Bitmap[placeInformationArray.length()];

			for (int i = 0; i < placeInformationArray.length(); i++) {
				JSONObject jsonObject = placeInformationArray.getJSONObject(i);
				names[i] = jsonObject.getString("name");
				categories[i] = jsonObject.getString("types");
				ratings[i] = "5.0";
				iconBitmaps[i] = DownloadImageHelper.downloadImage(jsonObject.getString("icon"));
			}
		}
		catch (JSONException e) {
			Log.e("[PlaceActivity] run()", "JSON error parsing data " + e.toString());
		}

		locationsListView = (ListView) findViewById(R.id.place_xml_listview_places);
		locationItem = new LocationItemAdapter(this, names, iconBitmaps, categories, ratings);
		locationsListView.setAdapter(locationItem);
		locationsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location loc) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	public Location getPhoneLocation() {
		// get location manager service
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// listen to that location
		MyLocationListener listener = new MyLocationListener();
		// request update for both GPS and NETWORK
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, listener);
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, listener);
		// return newest location
		return manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
}
