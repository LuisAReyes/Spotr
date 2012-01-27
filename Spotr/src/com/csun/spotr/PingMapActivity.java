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

public class PingMapActivity extends MapActivity {
	private static final String TAG = "(PingMapActivity)";
	private static final String GET_FRIEND_LOCATION_URL = "http://107.22.209.62/android/get_friend_locations.php";
	private static final String PING_ME_URL = "http://107.22.209.62/android/ping_me.php";

	private MapView mapView = null;
	private List<Overlay> mapOverlays = null;
	private CustomItemizedOverlay itemizedOverlay = null;
	private MapController mapController = null;
	private FineLocation fineLocation = new FineLocation();
	private Location lastKnownLocation = null;

	private Button buttonPing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ping_map);
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

		LocationResult locationResult = (new LocationResult() {
			@Override
			public void gotLocation(final Location location) {
				lastKnownLocation = location;
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
				new PingMeTask().execute(lastKnownLocation);
			}
		});

		// handle show display list event
		buttonShowFriends.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				new GetFriendLocationsTask().execute();
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

	private class GetFriendLocationsTask extends AsyncTask<Void, FriendAndLocation, Boolean> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(PingMapActivity.this);
			progressDialog.setMessage("Finding friends' locations...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(FriendAndLocation... friends) {
			OverlayItem overlay = new OverlayItem(new GeoPoint((int) (friends[0].getLatitude() * 1E6), (int) (friends[0].getLongitude() * 1E6)), friends[0].getName(), friends[0].getTime());
			// add to item to map
			itemizedOverlay.addOverlay(overlay, friends[0]);
			mapController.animateTo(new GeoPoint((int) (friends[0].getLatitude() * 1E6), (int) (friends[0].getLongitude() * 1E6)));
			mapController.setZoom(18);
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			userData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_FRIEND_LOCATION_URL, userData);
			if (array != null) {
				try {
					for (int i = 0; i < array.length(); ++i) {
						if (array.getJSONObject(i).has("users_tbl_latitude") && array.getJSONObject(i).has("users_tbl_longitude")) {
							publishProgress(
									new FriendAndLocation(
										array.getJSONObject(i).getInt("users_tbl_id"), 
										array.getJSONObject(i).getString("users_tbl_username"), 
										array.getJSONObject(i).getDouble("users_tbl_latitude"), 
										array.getJSONObject(i).getDouble("users_tbl_longitude"), 
										array.getJSONObject(i).getString("users_tbl_location_time")));
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
			progressDialog.dismiss();
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(PingMapActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("There is no friends' locations");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
		}
	}

	private class PingMeTask extends AsyncTask<Location, Void, String> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(PingMapActivity.this);
			progressDialog.setMessage("Ping me location...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Location... locations) {
			userData.add(new BasicNameValuePair("latitude", Double.toString(lastKnownLocation.getLatitude())));
			userData.add(new BasicNameValuePair("longitude", Double.toString(lastKnownLocation.getLongitude())));
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
			progressDialog.dismiss();
			
			OverlayItem ovl = 
					new OverlayItem(
						new GeoPoint(
							(int) (lastKnownLocation.getLatitude() * 1E6), 
							(int) (lastKnownLocation.getLongitude() * 1E6)), 
							CurrentUser.getCurrentUser().getUsername(), "Just Now");
			
			Drawable icon = getResources().getDrawable(R.drawable.map_circle_marker_red);
			icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
			ovl.setMarker(icon);
			
			FriendAndLocation yourLocation = new FriendAndLocation(
				CurrentUser.getCurrentUser().getId(), 
				CurrentUser.getCurrentUser().getUsername(), 
				lastKnownLocation.getLatitude(), 
				lastKnownLocation.getLongitude(), "just now");
			
			itemizedOverlay.addOverlay(ovl, yourLocation);
			mapController.animateTo(new GeoPoint((int) (lastKnownLocation.getLatitude() * 1E6), (int) (lastKnownLocation.getLongitude() * 1E6)));
			mapController.setZoom(18);

			AlertDialog dialogMessage = new AlertDialog.Builder(PingMapActivity.this).create();
			dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
			dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			if (result.equals("fail")) {
				dialogMessage.setMessage("Can't update your location due to network connection error.");
			}
			else {
				dialogMessage.setMessage("Ping your location successfully");
			}
			
			dialogMessage.show();
		}
	}

	private class CustomItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
		private List<OverlayItem> overlays = new ArrayList<OverlayItem>();
		private List<FriendAndLocation> friendLocationList = new ArrayList<FriendAndLocation>();
		private Context context;

		public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
			super(boundCenter(defaultMarker), mapView);
			context = mapView.getContext();
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
}
