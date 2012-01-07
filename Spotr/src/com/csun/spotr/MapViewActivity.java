package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.core.Place;
import com.csun.spotr.gui.BalloonItemizedOverlay;
import com.csun.spotr.helper.ImageHelper;
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
public class MapViewActivity extends MapActivity {
	private static final String TAG = "[MapViewActivity]";
	private static final String RADIUS = "50";
	private MapView mapView;
	private List<Overlay> mapOverlays;
	private MyItemizedOverlay itemizedOverlay;
	private MapController mapController;
	
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
		Drawable drawable = getResources().getDrawable(R.drawable.map_maker_red);
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

	private void processOverlayItems(Location currentLocation, String radius) {
		List<Place> placeList = new ArrayList<Place>();
		String url = GooglePlaceHelper.buildGooglePlacesUrl(currentLocation, radius);
		JSONObject json = JsonHelper.getJsonFromUrl(url);
		try {
			JSONArray placeInformationArray = json.getJSONArray("results");
			for (int i = 0; i < placeInformationArray.length(); i++) {
				String placeDetailsUrl = 
					GooglePlaceHelper.buildGooglePlaceDetailsUrl(placeInformationArray.getJSONObject(i).getString("reference"));
				JSONObject jsonTemp = JsonHelper.getJsonFromUrl(placeDetailsUrl);
				String address = jsonTemp.getJSONObject("result").getString("formatted_address");
				// construct a place
				Place place = new Place.Builder(
					placeInformationArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng"),
					placeInformationArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
					i)
					.name(placeInformationArray.getJSONObject(i).getString("name"))
					.address(address).build();
				
				placeList.add(place);
				OverlayItem overlay = new OverlayItem(new GeoPoint((int) (place.getLatitude() * 1E6), (int) (place.getLongitude() * 1E6)), place.getName(), place.getAddress());
				itemizedOverlay.addOverlay(overlay, place);
			}
		}
		catch (JSONException e) {
			Log.e(TAG + ".processOverlayItems(Location currentLocation, String radius)", "JSON error parsing data " + e.toString());
		}
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
		private MyLocationListener listener;
		private LocationManager manager;
		
		@Override
		protected Void doInBackground(String... radius) {
			// wait for a new location
			while (currentLocation == null) {
			}
			manager.removeUpdates(listener);
			processOverlayItems(currentLocation, radius[0]);
			return null;
		}

		@Override
		protected void onPreExecute() {
			listener = new MyLocationListener();
			manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// assume either GPS or Network is enabled
			// TODO: add error handling
			
			// prefer GPS
			if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
			}
			else {
				if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
				}
			}
			
			progressDialog = new ProgressDialog(MapViewActivity.this);
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
			return true;
		}
	}
}
