package com.csun.spotr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.skeleton.IActivityProgressUpdate;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.FineLocation;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.util.FineLocation.LocationResult;
import com.csun.spotr.core.FriendAndLocation;
import com.csun.spotr.core.Place;
import com.csun.spotr.custom_gui.BalloonItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Description:
 * 		Ping user's current location to friends or view friends' locations
 */
public class PingMapActivity 
	extends MapActivity 
		implements IActivityProgressUpdate<FriendAndLocation> {
	
	private static final 	String 						TAG = "(PingMapActivity)";
	private static final 	String 						GET_FRIEND_LOCATION_URL = "http://107.22.209.62/android/get_friend_locations.php";
	private static final 	String 						PING_ME_URL = "http://107.22.209.62/android/ping_me.php";

	private 				MapView 					mapView = null;
	private 				List<Overlay> 				mapOverlays = null;
	private 				CustomItemizedOverlay 		itemizedOverlay = null;
	private 				MapController 				mapController = null;
	private 				FineLocation 				fineLocation = new FineLocation();
	private 				Location 					lastKnownLocation = null;
	private 				Button 						buttonPing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.ping_map);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

		// get map view
		mapView = (MapView) findViewById(R.id.ping_map_xml_map);
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
		Button changeViewButton = (Button) findViewById(R.id.ping_map_xml_button_change_view);
		buttonPing = (Button) findViewById(R.id.ping_map_xml_button_ping_me);
		Button buttonShowFriends = (Button) findViewById(R.id.ping_map_xml_button_show_friends);
		
		buttonPing.setEnabled(false);

		LocationResult locationResult = (new LocationResult() {
			@Override
			public void gotLocation(final Location location) {
				lastKnownLocation = location;
				buttonPing.setEnabled(true);
			}
		});

		fineLocation.getLocation(this, locationResult);

		// handle change view event
		changeViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				startDialog();
			}
		});
		
		buttonPing.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				new PingMeTask(PingMapActivity.this).execute(lastKnownLocation);
			}
		});

		// handle show display list event
		buttonShowFriends.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				new GetFriendLocationsTask(PingMapActivity.this).execute();
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

	private static class GetFriendLocationsTask 
		extends AsyncTask<Void, FriendAndLocation, Boolean> 
			implements IAsyncTask<PingMapActivity> {
		
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private WeakReference<PingMapActivity> ref;
		
		public GetFriendLocationsTask(PingMapActivity a) {
			attach(a);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(FriendAndLocation... f) {
			ref.get().updateAsyncTaskProgress(f[0]);
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			userData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_FRIEND_LOCATION_URL, userData);
			if (array != null) {
				try {
					for (int i = 0; i < array.length(); ++i) {
						if (array.getJSONObject(i).has("users_locations_tbl_latitude") && array.getJSONObject(i).has("users_locations_tbl_longitude")) {
							publishProgress(
									new FriendAndLocation(
										array.getJSONObject(i).getInt("users_tbl_id"), 
										array.getJSONObject(i).getString("users_tbl_username"), 
										array.getJSONObject(i).getDouble("users_locations_tbl_latitude"), 
										array.getJSONObject(i).getDouble("users_locations_tbl_longitude"), 
										array.getJSONObject(i).getString("users_locations_tbl_created")));
						}
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFriendLocationTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == false) {
				Toast.makeText(ref.get().getApplicationContext(), "No friends found!", Toast.LENGTH_SHORT).show();
			}
			detach();
		}

		public void attach(PingMapActivity a) {
			ref = new WeakReference<PingMapActivity>(a);
		}

		public void detach() {
			ref.clear();
		}
	}

	private static class PingMeTask 
		extends AsyncTask<Location, Void, String> 
			implements IAsyncTask<PingMapActivity> {
		
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private WeakReference<PingMapActivity> ref;
		
		public PingMeTask(PingMapActivity a) {
			attach(a);
		}
		
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(Location... locations) {
			userData.add(new BasicNameValuePair("latitude", Double.toString(locations[0].getLatitude())));
			userData.add(new BasicNameValuePair("longitude", Double.toString(locations[0].getLongitude())));
			userData.add(new BasicNameValuePair("user_id", Integer.toString(CurrentUser.getCurrentUser().getId())));

			JSONObject json = JsonHelper.getJsonObjectFromUrlWithData(PING_ME_URL, userData);
			String result = "";
			try {
				result = json.getString("result");
			}
			catch (JSONException e) {
				Log.e(TAG + "PingMeTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			ref.get().displayUserLocation(ref.get().lastKnownLocation);
			if (result.equals("fail")) {
				Toast.makeText(ref.get().getApplicationContext(), "Can't update your location due to network connection error.", Toast.LENGTH_SHORT).show();
			}
			else {
				Toast.makeText(ref.get().getApplicationContext(), "Ping succeeded!", Toast.LENGTH_SHORT).show();
			}
		}
		
		public void attach(PingMapActivity a) {
			ref = new WeakReference<PingMapActivity>(a);
		}
		
		public void detach() {
			ref.clear();
		}
	}

	private static class CustomItemizedOverlay 
		extends BalloonItemizedOverlay<OverlayItem> {
		
		private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
		private List<FriendAndLocation> friendLocationList = new ArrayList<FriendAndLocation>();

		public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
			super(boundCenter(defaultMarker), mapView);
			populate();
		}

		public void addOverlay(OverlayItem overlay, FriendAndLocation fal) {
			overlays.add(overlay);
			friendLocationList.add(fal);
			populate();
		}

		public void clear() {
			overlays.clear();
			friendLocationList.clear();
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

	public void updateAsyncTaskProgress(FriendAndLocation f) {
		OverlayItem overlay = new OverlayItem(new GeoPoint((int) (f.getLatitude() * 1E6), (int) (f.getLongitude() * 1E6)), f.getName(), f.getTime());
		// add to item to map
		itemizedOverlay.addOverlay(overlay, f);
		mapController.animateTo(new GeoPoint((int) (f.getLatitude() * 1E6), (int) (f.getLongitude() * 1E6)));
		mapController.setZoom(18);
	}
	
	public void displayUserLocation(Location loc) {
		OverlayItem ovl = 
				new OverlayItem(
					new GeoPoint(
						(int) (loc.getLatitude() * 1E6), 
						(int) (loc.getLongitude() * 1E6)), 
						CurrentUser.getCurrentUser().getUsername(), "Just Now");
		
		Drawable icon = getResources().getDrawable(R.drawable.map_circle_marker_red);
		icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
		ovl.setMarker(icon);
		
		FriendAndLocation yourLocation = new FriendAndLocation(
			CurrentUser.getCurrentUser().getId(), 
			CurrentUser.getCurrentUser().getUsername(), 
			loc.getLatitude(), 
			loc.getLongitude(), "just now");
		
		itemizedOverlay.addOverlay(ovl, yourLocation);
		mapController.animateTo(new GeoPoint((int) (loc.getLatitude() * 1E6), (int) (loc.getLongitude() * 1E6)));
		mapController.setZoom(18);
	}
}
