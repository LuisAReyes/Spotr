package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.csun.spotr.core.PlaceInfo;
import com.csun.spotr.gui.BalloonItemizedOverlay;
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

	private MapView mapView;
	private List<Overlay> mapOverlays;
	private Drawable drawable;
	private MyItemizedOverlay itemizedOverlay;

	private Button changeViewButton;
	private Button showQuestButton;
	private Button locateButton;

	private Location currentLocation;
	private LocationManager manager;
	private MyLocationListener listener;
	private MapController mapController;
	
	private JSONObject json;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		
		// set up location manager and listener
		manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		
		// get map view
		mapView = (MapView) findViewById(R.id.mapview_xml_map);
		
		// get map controller
		mapController = mapView.getController();
		
		// set zoom button
		mapView.setBuiltInZoomControls(true);
		
		// get overlays
		mapOverlays = mapView.getOverlays();
		// get the display icon on map
		drawable = getResources().getDrawable(R.drawable.map_maker_red);
		// initialize overlay item
		itemizedOverlay = new MyItemizedOverlay(drawable, mapView);
		// add them to the map
		mapOverlays.add(itemizedOverlay);
		
		// get the other 3 buttons
		changeViewButton = (Button) findViewById(R.id.mapview_xml_button_change_view);
		showQuestButton = (Button) findViewById(R.id.mapview_xml_button_places);
		locateButton = (Button) findViewById(R.id.mapview_xml_button_locate);
		
		currentLocation = getPhoneLocation();
		processOverlayItems(currentLocation, "1");

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
				currentLocation = getPhoneLocation();
				processOverlayItems(currentLocation, "5");
				mapController.animateTo(new GeoPoint((int) (currentLocation.getLatitude() * 1E6), (int) (currentLocation.getLongitude() * 1E6)));
				mapController.setZoom(16);
			}
		});

		// handle show quest event
		showQuestButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				itemizedOverlay.clear();
				currentLocation = getPhoneLocation();
				processOverlayItems(currentLocation, "500");
				mapController.animateTo(new GeoPoint((int) (currentLocation.getLatitude() * 1E6), (int) (currentLocation.getLongitude() * 1E6)));
				mapController.setZoom(16);
			}
		});
	}

	private void processOverlayItems(Location currentLocation, String radius) {
		String url = GooglePlaceHelper.buildGooglePlaceUrl(currentLocation, radius);
		json = JsonHelper.getJSONfromURL(url);

		String name = "name";
		String vicinity = "vicinity";
		String types = "types";
		try {
			JSONArray jsonPlaceArray = json.getJSONArray("results");
			for (int i = 0; i < jsonPlaceArray.length(); ++i) {
				JSONObject jsonObject = jsonPlaceArray.getJSONObject(i);
				double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
				double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

				if (jsonObject.getString("name") != null) {
					name = jsonObject.getString("name");
				}

				// temporary set as types! since vicinity sometimes doesn't exists
				if (jsonObject.getString("types") != null) {
					vicinity = jsonObject.getString("types");
				}

				if (jsonObject.getString("types") != null) {
					types = jsonObject.getString("types");
				}

				PlaceInfo placeInfo = new PlaceInfo(name, vicinity, types, Double.toString(lat) + Double.toString(lng));
				OverlayItem overlay = new OverlayItem(new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6)), name, vicinity);
				itemizedOverlay.addOverlay(overlay, placeInfo);
			}
		}
		catch (JSONException e) {
			Log.e("Json", "Error passing data" + e.toString());
		}
	}

	private void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Map View Option");
		myAlertDialog.setMessage("Pick a map view");
		myAlertDialog.setPositiveButton("Street", new DialogInterface.OnClickListener() {
			// do something when the button is clicked
			public void onClick(DialogInterface arg0, int arg1) {
				mapView.setStreetView(true);
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

	private Location getPhoneLocation() {
		// request update for both GPS and NETWORK
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 0, listener);
		manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 0, listener);
		// return newest location
		return manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	private class MyItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
		private Vector<PlaceInfo> placeInformations = new Vector<PlaceInfo>();
		private Context context;

		public MyItemizedOverlay(Drawable defaultMarker, MapView mapView) {
			super(boundCenter(defaultMarker), mapView);
			context = mapView.getContext();
		}

		public void addOverlay(OverlayItem overlay, PlaceInfo placeInfo) {
			overlays.add(overlay);
			placeInformations.add(placeInfo);
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
			Intent intent = new Intent("com.csun.spotr.PlaceDetailActivity");
			intent.putExtra("place_name", placeInformations.get(index).getName());
			intent.putExtra("place_vicinity", placeInformations.get(index).getVicinity());
			intent.putExtra("place_category", placeInformations.get(index).getCategory());
			intent.putExtra("place_coordinate", placeInformations.get(index).getCoordinate());
			startActivity(intent);
			return true;
		}
	}
}
