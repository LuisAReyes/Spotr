package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.csun.spotr.core.Place;
import com.csun.spotr.gui.PlaceItemAdapter;

/**
 * @author: Chan Nguyen
 */
public class PlaceActivity extends Activity {
	private final String 			  	TAG = "[PlaceActivity]";
	private final String				radius = "500";
	private       ListView            	placesListView;
	private       PlaceItemAdapter      placeItemAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set up layout
		setContentView(R.layout.place);
		// make sure keyboard of edit text do not populate
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// initially, load all locations
		if (!startService()) {
			CreateAlert("Unexpected Error!", "Service cannot be started.");
		}
		else {
			// Toast.makeText(PlaceActivity.this, "Service Started", Toast.LENGTH_LONG).show();
		}
		// register click event for refresh button
		ImageButton refreshButton = (ImageButton) findViewById(R.id.place_xml_button_refresh);
		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startService();
			}
		});
	}
	
	public AlertDialog CreateAlert(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		return alert;

	}
	
	public boolean startService() {
		try {
			UpdateLocationTask task = new UpdateLocationTask();
			task.execute();
			return true;
		}
		catch (Exception error) {
			return false;
		}
	}
	
	private class UpdateLocationTask extends AsyncTask<Void, Integer, List<Place>> {
		public   Location  			currentLocation = null;
		private  ProgressDialog 	progressDialog = null;
		
		@Override
		protected List<Place> doInBackground(Void...voids) {
			// wait for a new location
			while(currentLocation == null) {
				
			}
			// get current location and passing it to get list of places
			return retrievePlacesFromGoogle(currentLocation);
		}
		
		@Override
		protected void onPreExecute() {
			MyLocationListener myLocationListener = new MyLocationListener();
			myLocationListener = new MyLocationListener();
			LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActivity.this);
			progressDialog.setMessage("Loading from Google Places...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
	
		@Override
		protected void onPostExecute(List<Place> placeList) {
			progressDialog.dismiss();
			placesListView = (ListView) findViewById(R.id.place_xml_listview_places);
			placeItemAdapter = new PlaceItemAdapter(PlaceActivity.this, placeList);
			placesListView.setAdapter(placeItemAdapter);
			placesListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// handle click 
				}
			});
		}
		
		private List<Place> retrievePlacesFromGoogle(Location location) {
			List<Place> placeList = new ArrayList<Place>();
			String url = GooglePlaceHelper.buildGooglePlacesUrl(location, radius);
			JSONObject json = JsonHelper.getJsonFromUrl(url);
			try {
				JSONArray placeInformationArray = json.getJSONArray("results");
				for (int i = 0; i < placeInformationArray.length(); i++) {
					JSONObject jsonObject = placeInformationArray.getJSONObject(i);
					double longitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
					double latitude = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
					String id = jsonObject.getString("id");
					String name = jsonObject.getString("name");
					String types = jsonObject.getString("types");
					String reference = jsonObject.getString("reference");
					String iconUrl = jsonObject.getString("icon");
					
					// build detailed place url
					String placeDetailsUrl = GooglePlaceHelper.buildGooglePlaceDetailsUrl(reference);
					// get json data
					JSONObject jsonTemp = JsonHelper.getJsonFromUrl(placeDetailsUrl);
					String address = jsonTemp.getJSONObject("result").getString("formatted_address");
					// String phoneNumber = jsonTemp.getJSONObject("result").getString("formatted_phone_number");
					// String websiteUrl = jsonTemp.getJSONObject("result").getString("website");
					
					// construct a place
					Place place = new Place.Builder(longitude, latitude, i)
						.googleId(id)
						.name(name)
						.types(types)
						.iconUrl(iconUrl)
						.address(address)
						.build();
					
					placeList.add(place);
				}
			}
			catch (JSONException e) {
				Log.e(TAG + ".[UpdateLocationTask].retrievePlacesFromGoogle(Location location)", "JSON error parsing data " + e.toString());
			}
			return placeList;
		}
		
		public class MyLocationListener implements LocationListener {
			public void onLocationChanged(Location location) {
				// update new location
				currentLocation = location;
			}

			public void onProviderDisabled(String provider) {
				Log.i("OnProviderDisabled", "OnProviderDisabled");
			}

			public void onProviderEnabled(String provider) {
				Log.i("onProviderEnabled", "onProviderEnabled");
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.i("onStatusChanged", "onStatusChanged");
			}
		}
	}
}
