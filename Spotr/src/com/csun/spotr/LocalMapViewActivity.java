package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.csun.spotr.core.CurrentUser;
import com.csun.spotr.core.Place;
import com.csun.spotr.gui.BalloonItemizedOverlay;
import com.csun.spotr.helper.DownloadImageHelper;
import com.csun.spotr.helper.GooglePlaceHelper;
import com.csun.spotr.helper.JsonHelper;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * @author: Chan Nguyen
 */
public class LocalMapViewActivity extends MapActivity {
	private static final String TAG = "[LocalMapViewActivity]";
	private static final String URL = "http://107.22.209.62/android/get_spots.php";
	private static final String RADIUS = "500";
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private MyItemizedOverlay itemizedOverlay;
	MapController mapController;
	
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
		itemizedOverlay = new MyItemizedOverlay(drawable, mapView);
		// add them to the map
		mapOverlays.add(itemizedOverlay);

		// get the other 3 buttons
		Button changeViewButton = (Button) findViewById(R.id.mapview_xml_button_change_view);
		Button listPlaceButton = (Button) findViewById(R.id.mapview_xml_button_places);
		Button locateButton = (Button) findViewById(R.id.mapview_xml_button_locate);

		UpdateLocationTask task = new UpdateLocationTask();
		task.execute("1");

		// handle change view event
		changeViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startDialog();
			}
		});

		// handle locate event
		locateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				itemizedOverlay.clear();
				UpdateLocationTask task = new UpdateLocationTask();
				task.execute("1");
			}
		});

		// handle show display list event
		listPlaceButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				itemizedOverlay.clear();
				UpdateLocationTask task = new UpdateLocationTask();
				task.execute(RADIUS);
			}
		});
	}

	private List<Place> filterPlaces(Location currentLocation, String radius) {
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
				// create an item overlay based on this location
				OverlayItem overlay = new OverlayItem(new GeoPoint((int) (place.getLatitude() * 1E6), (int) (place.getLongitude() * 1E6)), place.getName(), place.getAddress());
				// add to item to map 
				itemizedOverlay.addOverlay(overlay, place);
			}
		}
		catch (JSONException e) {
			Log.e(TAG + ".filterPlaces() : ", "JSON error parsing data" + e.toString());
		}
		return placeList;
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

	private class UpdateLocationTask extends AsyncTask<String, Integer, Void> {
		private ProgressDialog progressDialog;
		private Location currentLocation;
		@Override
		protected Void doInBackground(String... radius) {
			// wait for a new location
			while (currentLocation == null) {
			}
			filterPlaces(currentLocation, radius[0]);
			return null;
		}

		@Override
		protected void onPreExecute() {
			MyLocationListener myLocationListener = new MyLocationListener();
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// assume either GPS or Network is enabled
			// TODO: add error handling
			
			// prefer GPS
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
			}
			// otherwise use Network connection
			else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
			}
			
			progressDialog = new ProgressDialog(LocalMapViewActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void unused) {
			progressDialog.dismiss();
			mapController.animateTo(new GeoPoint((int) (currentLocation.getLatitude() * 1E6), (int) (currentLocation.getLongitude() * 1E6)));
			mapController.setZoom(16);
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

	private class MyItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
		private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
		private List<Place> placeInformations = new ArrayList<Place>();
		private Context context;

		public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
			super(boundCenter(defaultMarker), mapView);
			context = mapView.getContext();
		}

		public void addOverlay(OverlayItem overlay, Place place) {
			overlays.add(overlay);
			placeInformations.add(place);
			populate();
		}

		public void clear() {
			overlays.clear();
			placeInformations.clear();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return overlays.get(i);
		}

		@Override
		public int size() {
			return overlays.size();
		}

		@Override
		protected boolean onBalloonTap(int index, OverlayItem item) {
			Intent intent = new Intent("com.csun.spotr.PlaceMainActivity");
			startActivity(intent);
			return true;
		}
	}
}
