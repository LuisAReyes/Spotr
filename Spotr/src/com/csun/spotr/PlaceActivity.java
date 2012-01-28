package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.ListView;
import android.util.Log;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.location.Location;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.core.adapter_item.PlaceItem;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.FineLocation;
import com.csun.spotr.util.FineLocation.LocationResult;
import com.csun.spotr.util.GooglePlaceHelper;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.adapter.PlaceItemAdapter;

public class PlaceActivity extends Activity {
	private static final String TAG = "(PlaceActivity)";
	private static final String GET_SPOTS_URL = "http://107.22.209.62/android/get_spots.php";
	private static final String UPDATE_GOOGLE_PLACES_URL = "http://107.22.209.62/android/update_google_places.php";
	
	private ListView list;
	private PlaceItemAdapter adapter;
	private List<PlaceItem> placeItemList = new ArrayList<PlaceItem>();
	private Location lastKnownLocation = null;
	private FineLocation fineLocation = new FineLocation();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "I'm created!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place);
		// make sure keyboard of edit text do not populate
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	
		list = (ListView) findViewById(R.id.place_xml_listview_places);
		adapter = new PlaceItemAdapter(PlaceActivity.this, placeItemList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent("com.csun.spotr.PlaceMainActivity");
				Bundle extras = new Bundle();
				extras.putInt("place_id", placeItemList.get(position).getId());
				intent.putExtras(extras);
				startActivity(intent);
				onPause();
			}
		});
		
		// register click event for refresh button
		Button refreshButton = (Button) findViewById(R.id.place_xml_button_refresh);
		refreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				placeItemList = new ArrayList<PlaceItem>();
				adapter.notifyDataSetChanged();
				new GetSpotsTask().execute();
			}
		});
		
		LocationResult locationResult = (new LocationResult() {
			@Override
			public void gotLocation(final Location location) {
				lastKnownLocation = location;
				new GetSpotsTask().execute();
			}
		});
		
		fineLocation.getLocation(this, locationResult);
	}

	public AlertDialog CreateAlert(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		return alert;
	}

	private class GetSpotsTask extends AsyncTask<Void, PlaceItem, Boolean> {
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
			Log.v(TAG, "loading places from database...");
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActivity.this);
			progressDialog.setMessage("Loading places...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(PlaceItem... places) {
			progressDialog.dismiss();
			placeItemList.add(places[0]);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			// send Google data to our server to update 'spots' table
			JsonHelper.getJsonObjectFromUrlWithData(UPDATE_GOOGLE_PLACES_URL, constructGooglePlace());
			
			// now sending latitude, longitude and radius to retrieve places
			placeData.add(new BasicNameValuePair("latitude", Double.toString(lastKnownLocation.getLatitude())));
			placeData.add(new BasicNameValuePair("longitude", Double.toString(lastKnownLocation.getLongitude())));
			placeData.add(new BasicNameValuePair("radius", GooglePlaceHelper.RADIUS_IN_KM));
			
			// get places as JSON format from our database
			JSONArray jsonPlaceArray = JsonHelper.getJsonArrayFromUrlWithData(GET_SPOTS_URL, placeData);
			if (jsonPlaceArray != null) {
				try {
					for (int i = 0; i < jsonPlaceArray.length(); ++i) {
						publishProgress(
							new PlaceItem(
								jsonPlaceArray.getJSONObject(i).getInt("spots_tbl_id"), 
								jsonPlaceArray.getJSONObject(i).getString("spots_tbl_name"), 
								jsonPlaceArray.getJSONObject(i).getString("spots_tbl_description")));
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetSpotsTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
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
		case R.id.options_menu_xml_item_setting_icon :
			intent = new Intent("com.csun.spotr.SettingsActivity");
			startActivity(intent);
			break;
		case R.id.options_menu_xml_item_logout_icon :
			SharedPreferences.Editor editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
			editor.clear();
			editor.commit();
			intent = new Intent("com.csun.spotr.LoginActivity");
			startActivity(intent);
			break;
		case R.id.options_menu_xml_item_mainmenu_icon :
			intent = new Intent("com.csun.spotr.MainMenuActivity");
			startActivity(intent);
			break;
		}
		return true;
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
}
