package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.FineLocation;
import com.csun.spotr.util.GooglePlaceHelper;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.util.FineLocation.LocationResult;
import com.csun.spotr.core.Place;
import com.csun.spotr.custom_gui.BalloonItemizedOverlay;
import com.csun.spotr.custom_gui.CustomItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocalMapViewActivity extends MapActivity {
	private static final String TAG = "(LocalMapViewActivity)";
	private static final String GET_SPOTS_URL = "http://107.22.209.62/android/get_spots.php";
	private static final String UPDATE_GOOGLE_PLACES_URL = "http://107.22.209.62/android/update_google_places.php";

	private MapView mapView = null;
	private List<Overlay> mapOverlays = null;
	private CustomItemizedOverlay itemizedOverlay = null;
	private MapController mapController = null;
	private FineLocation fineLocation = new FineLocation();
	private Location lastKnownLocation = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		// get map view
		mapView = (MapView) findViewById(R.id.mapview_xml_map);
		// get map controller
		mapController = mapView.getController();
		// set zoom button
		mapView.setBuiltInZoomControls(true);
		// get overlays
		mapOverlays = mapView.getOverlays();
		// get the display icon on map
		Drawable drawable = getResources().getDrawable(R.drawable.map_maker_green);
		// initialize overlay item
		itemizedOverlay = new CustomItemizedOverlay(drawable, mapView);
		// add them to the map
		mapOverlays.add(itemizedOverlay);

		// get the other 3 buttons
		Button changeViewButton = (Button) findViewById(R.id.mapview_xml_button_change_view);
		Button listPlaceButton = (Button) findViewById(R.id.mapview_xml_button_places);
		Button locateButton = (Button) findViewById(R.id.mapview_xml_button_locate);

		LocationResult locationResult = (new LocationResult() {
			@Override
			public void gotLocation(final Location location) {
				lastKnownLocation = location;
				OverlayItem ovl = new OverlayItem(new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6)), "My location", "Hello");
				Drawable icon = getResources().getDrawable(R.drawable.map_circle_marker_red);
				icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
				ovl.setMarker(icon);
				Place place = new Place.Builder(location.getLongitude(), location.getLatitude(), -1).build();
				itemizedOverlay.addOverlay(ovl, place);

				mapController.animateTo(new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6)));
				mapController.setZoom(18);

				// process to update map
				new GetSpotsTask().execute(lastKnownLocation);
			}
		});
		fineLocation.getLocation(this, locationResult);

		// handle change view event
		changeViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startDialog();
			}
		});

		// handle locate event
		locateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				// do nothing
			}
		});

		// handle show display list event
		listPlaceButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				// do nothing
			}
		});
	}

	private void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Map View Option");
		myAlertDialog.setMessage("Pick a map view");
		myAlertDialog.setPositiveButton("Street", new DialogInterface.OnClickListener() {
			// do something when the button is clicked
			public void onClick(DialogInterface arg0, int arg1) {
				mapView.setSatellite(false);
				mapView.setTraffic(false);
				mapView.invalidate();
			}
		});

		myAlertDialog.setNeutralButton("Satellite", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				mapView.setSatellite(true);
				mapView.setTraffic(false);
				mapView.invalidate();
			}
		});

		myAlertDialog.setNegativeButton("Traffic", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				mapView.setSatellite(false);
				mapView.setTraffic(true);
				mapView.invalidate();
			}
		});
		myAlertDialog.show();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private class GetSpotsTask extends AsyncTask<Location, Place, Boolean> {
		private List<NameValuePair> placeData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

		private List<NameValuePair> constructGooglePlace() {
			// this is data we will send to our server
			List<NameValuePair> sentData = new ArrayList<NameValuePair>();
			// we reformat the original data to include only what we need
			JSONArray reformattedData = new JSONArray();
			JSONObject json = JsonHelper.getJsonFromUrl(GooglePlaceHelper.buildGooglePlacesUrl(lastKnownLocation, GooglePlaceHelper.GOOGLE_RADIUS_IN_METER));
			JSONObject temp = null;
			try {
				JSONArray originalGoogleDataArray = json.getJSONArray("results");
				for (int i = 0; i < originalGoogleDataArray.length(); i++) {
					// id: is used to verify place existence 
					JSONObject e = new JSONObject();
					e.put("id", originalGoogleDataArray.getJSONObject(i).getString("id"));
					e.put("name", originalGoogleDataArray.getJSONObject(i).getString("name"));
					e.put("lat", originalGoogleDataArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
					e.put("lon", originalGoogleDataArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
					temp = JsonHelper.getJsonFromUrl(GooglePlaceHelper.buildGooglePlaceDetailsUrl(originalGoogleDataArray.getJSONObject(i).getString("reference")));
					
					if (temp.getJSONObject("result").has("formatted_address")) {
						e.put("addr", temp.getJSONObject("result").getString("formatted_address"));
					}
					else {
						e.put("addr", "default address");
					}
					
					if (temp.getJSONObject("result").has("formatted_phone_number")) {
						e.put("phone", temp.getJSONObject("result").getString("formatted_phone_number"));
					}
					else {
						e.put("phone", "(888) 888-8888");
					}
					
					if (temp.getJSONObject("result").has("url")) {
						e.put("url", temp.getJSONObject("result").getString("url"));
					}
					else {
						e.put("url", "https://www.google.com/");
					}
					
					// put e
					reformattedData.put(e);
				}
			}
			catch (JSONException e) {
				Log.e(TAG + "GetSpotsTask.constructGooglePlace() : ", "JSON error parsing data" + e.toString());
			}
			// send data to our server
			sentData.add(new BasicNameValuePair("google_array", reformattedData.toString()));
			return sentData;
		}
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(LocalMapViewActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Place... places) {
			OverlayItem overlay = new OverlayItem(new GeoPoint((int) (places[0].getLatitude() * 1E6), (int) (places[0].getLongitude() * 1E6)), places[0].getName(), places[0].getAddress());
			// add to item to map
			itemizedOverlay.addOverlay(overlay, places[0]);
		}

		@Override
		protected Boolean doInBackground(Location... locations) {
			// send Google data to our server to update 'spots' table
			JsonHelper.getJsonObjectFromUrlWithData(UPDATE_GOOGLE_PLACES_URL, constructGooglePlace());
			
			// now sending latitude, longitude and radius to retrieve places
			placeData.add(new BasicNameValuePair("latitude", Double.toString(locations[0].getLatitude())));
			placeData.add(new BasicNameValuePair("longitude", Double.toString(locations[0].getLongitude())));
			placeData.add(new BasicNameValuePair("radius", GooglePlaceHelper.RADIUS_IN_KM));
			
			// get places as JSON format from our database
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
										.address(array.getJSONObject(i).getString("spots_tbl_description")).build());
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetSpotsTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
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
				AlertDialog dialogMessage = new AlertDialog.Builder(LocalMapViewActivity.this).create();
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
	public void onRestart() {
		Log.v(TAG, "I'm restarted!");
		super.onRestart();
	}

	@Override
	public void onStop() {
		Log.v(TAG, "I'm stopped!");
		super.onStop();
	}

	@Override
	public void onPause() {
		Log.v(TAG, "I'm paused!");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "I'm destroyed!");
		super.onDestroy();
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