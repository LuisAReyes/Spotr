package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.util.Log;

import android.content.Context;
import android.content.DialogInterface;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.helper.GooglePlaceHelper;
import com.csun.spotr.helper.JsonHelper;
import com.csun.spotr.core.Place;
import com.csun.spotr.gui.PlaceItemAdapter;

public class PlaceActivity extends Activity {
	private static final String TAG = "[PlaceActivity]";
	private static final String	RADIUS = "2000";
	private ListView list;
	private PlaceItemAdapter adapter;
	private List<Place> placeList = new ArrayList<Place>();
	
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
		
		list = (ListView) findViewById(R.id.place_xml_listview_places);
		adapter = new PlaceItemAdapter(PlaceActivity.this, placeList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
	
		// register click event for refresh button
		Button refreshButton = (Button) findViewById(R.id.place_xml_button_refresh);
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
	
	private class UpdateLocationTask extends AsyncTask<Void, Integer, Location> {
		public Location currentLocation = null;
		private ProgressDialog progressDialog = null;
		private MyLocationListener listener;
		private LocationManager manager;
		
		@Override
		protected Location doInBackground(Void...voids) {
			// wait for a new location
			while(currentLocation == null) {
				
			}
			manager.removeUpdates(listener);
			return currentLocation;
		}
		
		@Override
		protected void onPreExecute() {
			listener = new MyLocationListener();
			manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// assume either GPS or Network is enabled
			// TODO: add error handling
			
			/*
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
			}
			*/
			// NETWORK is faster
			if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 
				manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
			
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActivity.this);
			progressDialog.setMessage("Loading Google signal...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
	
		@Override
		protected void onPostExecute(Location location) {
			progressDialog.dismiss();
			new GetGooglePlacesTask().execute(location);
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
	
	private class GetGooglePlacesTask extends AsyncTask<Location, Place, Boolean> {
		private ProgressDialog progressDialog = null;
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
	    protected void onProgressUpdate(Place... places) {
			placeList.add(places[0]);
			adapter.notifyDataSetChanged();
			// adapter.notifyDataSetInvalidated();
	    }
		
		@Override
		protected Boolean doInBackground(Location...locations) {
			String url = GooglePlaceHelper.buildGooglePlacesUrl(locations[0], RADIUS);
			JSONObject json = JsonHelper.getJsonFromUrl(url);
			if (json != null) {
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
						publishProgress(
							new Place.Builder(longitude, latitude, i)
									.googleId(id)
									.name(name)
									.types(types)
									.iconUrl(iconUrl)
									.address(address)
									.build());
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFriendTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(PlaceActivity.this).create();
				dialogMessage.setTitle("Hello ");
				dialogMessage.setMessage("There are no places at this location");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
		}
			
	}
}
