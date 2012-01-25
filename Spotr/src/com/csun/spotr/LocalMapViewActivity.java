package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

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
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.util.FineLocation.LocationResult;
import com.csun.spotr.core.Place;
import com.csun.spotr.custom_gui.BalloonItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class LocalMapViewActivity extends MapActivity {
	private static final String TAG = "(LocalMapViewActivity)";
	private static final String GET_SPOTS_URL = "http://107.22.209.62/android/get_spots.php";
	private static final String GOOGLE_RADIUS_IN_METER = "100";
	private static final String RADIUS_IN_KM = "0.1";
	
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
				OverlayItem ovl = new OverlayItem(
						new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6)), "My location", "Hello");
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
			OverlayItem overlay = new OverlayItem(
				new GeoPoint((int) (places[0].getLatitude() * 1E6), (int) (places[0].getLongitude() * 1E6)), places[0].getName(), places[0].getAddress());
			// add to item to map 
			itemizedOverlay.addOverlay(overlay, places[0]);
	    }
		
		@Override
		protected Boolean doInBackground(Location...locations) {
			placeData.add(new BasicNameValuePair("latitude", Double.toString(locations[0].getLatitude())));
			placeData.add(new BasicNameValuePair("longitude", Double.toString(locations[0].getLongitude())));
			placeData.add(new BasicNameValuePair("radius", RADIUS_IN_KM)); 
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
	
	private class CustomItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
		private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
		private List<Place> places = new ArrayList<Place>();
		private Context context;

		public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
			super(boundCenter(defaultMarker), mapView);
			context = mapView.getContext();
		}

		public void addOverlay(OverlayItem overlay, Place place) {
			overlays.add(overlay);
			places.add(place);
			populate();
		}

		public void clear() {
			overlays.clear();
			places.clear();
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
			if (places.get(index).getId() != -1) {
				Intent intent = new Intent("com.csun.spotr.PlaceMainActivity");
				Bundle extras = new Bundle();
				extras.putInt("place_id", places.get(index).getId());
				intent.putExtras(extras);
				startActivity(intent);
			}
			return true;
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
