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
import android.content.Intent;
import android.graphics.Bitmap;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.helper.DownloadImageHelper;
import com.csun.spotr.helper.GooglePlaceHelper;
import com.csun.spotr.helper.JsonHelper;
import com.csun.spotr.core.Place;
import com.csun.spotr.gui.PlaceItemAdapter;
import com.google.android.maps.GeoPoint;

/**
 * @author: Chan Nguyen
 */
public class LocalPlaceActivity extends Activity {
	private static final String 		TAG = "[LocalPlaceActivity]";
	private static final String 		URL = "http://107.22.209.62/android/get_spots.php";
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
		public   Location currentLocation = null;
		private  ProgressDialog progressDialog = null;
		private MyLocationListener listener;
		private LocationManager manager;
		
		@Override
		protected List<Place> doInBackground(Void...voids) {
			// wait for a new location
			while(currentLocation == null) {
				
			}
			manager.removeUpdates(listener);
			// get current location and passing it to get list of places
			return retrievePlaces(currentLocation, radius);
		}
		
		@Override
		protected void onPreExecute() {
			listener = new MyLocationListener();
			manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// assume either GPS or Network is enabled
			// TODO: add error handling
			
			// prefer GPS
			// prefer GPS
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
			}
			else {
				if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
				}
			}
			// display waiting dialog
			progressDialog = new ProgressDialog(LocalPlaceActivity.this);
			progressDialog.setMessage("Loading local places...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
	
		@Override
		protected void onPostExecute(final List<Place> placeList) {
			progressDialog.dismiss();
			placesListView = (ListView) findViewById(R.id.place_xml_listview_places);
			placeItemAdapter = new PlaceItemAdapter(LocalPlaceActivity.this, placeList);
			placesListView.setAdapter(placeItemAdapter);
			placesListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent("com.csun.spotr.PlaceMainActivity");
					Bundle extras = new Bundle();
					extras.putInt("place_id", placeList.get(position).getId());
					intent.putExtras(extras);
					startActivity(intent);
				}
			});
		}
		
		private List<Place> retrievePlaces(Location currentLocation, String radius) {
			List<Place> placeList = new ArrayList<Place>();
			List<NameValuePair> datas = new ArrayList<NameValuePair>();
			datas.add(new BasicNameValuePair("latitude", Double.toString(currentLocation.getLatitude())));
			datas.add(new BasicNameValuePair("longitude", Double.toString(currentLocation.getLongitude())));
			datas.add(new BasicNameValuePair("radius", radius)); 
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(URL, datas);
			
			int id = 0;
			double longitude = 0.0;
			double latitude = 0.0;
			String name = "";
			String description = "";
			
			try {
				for (int i = 0; i < array.length(); ++i) { 
					JSONObject temp = array.getJSONObject(i);
					id = temp.getInt("id");
					longitude = temp.getDouble("longitude");
					latitude = temp.getDouble("latitude");
					name = temp.getString("name");
					description = temp.getString("description");
					// create a place
					Place place = new Place.Builder(longitude, latitude, id).name(name).address(description).build();
					// add to list of places
					placeList.add(place);
				}
			}
			catch (JSONException e) {
				Log.e(TAG + ".filterPlaces() : ", "JSON error parsing data" + e.toString());
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