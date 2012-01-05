package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.core.Place;
import com.csun.spotr.gui.PlaceItemAdapter;
import com.google.android.maps.GeoPoint;

public class LocalPlaceActivity extends Activity {
	private static final String TAG = "[LocalPlaceActivity]";
	private static final String GET_SPOTS_URL = "http://107.22.209.62/android/get_spots.php";
	private static final String	RADIUS = "100";
	private ListView list;
	private PlaceItemAdapter adapter;
	private List<Place> placeList = new ArrayList<Place>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place);
		// make sure keyboard of edit text do not populate
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		// initially, load all locations
		if (!startService()) {
			CreateAlert("Unexpected Error!", "Service cannot be started.");
		}
	
		// register click event for refresh button
		Button refreshButton = (Button) findViewById(R.id.place_xml_button_refresh);
		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startService();
			}
		});
		
		list = (ListView) findViewById(R.id.place_xml_listview_places);
		adapter = new PlaceItemAdapter(LocalPlaceActivity.this, placeList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent("com.csun.spotr.PlaceMainActivity");
				Bundle extras = new Bundle();
				extras.putInt("place_id", placeList.get(position).getId());
				intent.putExtras(extras);
				startActivity(intent);
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
			progressDialog = new ProgressDialog(LocalPlaceActivity.this);
			progressDialog.setMessage("Loading Google signal...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
	
		@Override
		protected void onPostExecute(Location location) {
			progressDialog.dismiss();
			new GetSpotsTask().execute(location);
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
	
	private class GetSpotsTask extends AsyncTask<Location, Place, Boolean> {
		private List<NameValuePair> placeData = new ArrayList<NameValuePair>(); 
		private ProgressDialog progressDialog = null;
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(LocalPlaceActivity.this);
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
			placeData.add(new BasicNameValuePair("latitude", Double.toString(locations[0].getLatitude())));
			placeData.add(new BasicNameValuePair("longitude", Double.toString(locations[0].getLongitude())));
			placeData.add(new BasicNameValuePair("radius", RADIUS)); 
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_SPOTS_URL, placeData);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) { 
						publishProgress(
							new Place.Builder(
								// require parameters
								array.getJSONObject(i).getDouble("spots_tbl_longitude"), 
								array.getJSONObject(i).getDouble("spots_tbl_latitude"), 
								array.getJSONObject(i).getInt("spots_tbl_id"))
									// optional parameters
									.name(array.getJSONObject(i).getString("spots_tbl_name"))
									.address(array.getJSONObject(i).getString("spots_tbl_description"))
										.build());
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFriendTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(LocalPlaceActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.options_menu_xml_item_setting_icon:
				intent = new Intent("com.csun.spotr.SettingsActivity");
				startActivity(intent);
				break;
			case R.id.options_menu_xml_item_logout_icon:
				SharedPreferences.Editor editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
				editor.clear();
				editor.commit();
				intent = new Intent("com.csun.spotr.LoginActivity");
				startActivity(intent);
				break;
			case R.id.options_menu_xml_item_mainmenu_icon:
				intent = new Intent("com.csun.spotr.MainMenuActivity");
				startActivity(intent);
				break;
		}
		return true;
	}
}
