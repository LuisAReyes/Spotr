package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.core.Place;
import com.csun.spotr.custom_gui.BalloonItemizedOverlay;
import com.csun.spotr.util.JsonHelper;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceInfoActivity extends MapActivity {
    private static final String TAG = "(PlaceInfoActivity)";
    private static final String GET_SPOT_DETAIL_URL = "http://107.22.209.62/android/get_spot_detail.php";
    private int currentPlaceId = 0;
    private MapView mapView = null;
    private List<Overlay> mapOverlays = null;
    private MapController mapController = null;
    private MyItemizedOverlay itemizedOverlay = null;
    private Drawable mapMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.place_info);

	mapView = (MapView) findViewById(R.id.place_info_xml_mapview);
	mapController = mapView.getController();
	mapView.setBuiltInZoomControls(true);
	mapView.setSatellite(false);
	mapView.invalidate();

	mapOverlays = mapView.getOverlays();
	mapMarker = getResources().getDrawable(R.drawable.map_maker_red);
	itemizedOverlay = new MyItemizedOverlay(mapMarker, mapView);
	mapOverlays.add(itemizedOverlay);

	// get place id
	Bundle extrasBundle = getIntent().getExtras();
	currentPlaceId = extrasBundle.getInt("place_id");

	new GetPlaceDetailTask().execute();
    }

    private class GetPlaceDetailTask extends AsyncTask<Void, Integer, Place> {
	private List<NameValuePair> placeData = new ArrayList<NameValuePair>();
	private ProgressDialog progressDialog = null;
	private JSONArray array;

	@Override
	protected Place doInBackground(Void... voids) {
	    placeData.add(new BasicNameValuePair("place_id", Integer.toString(currentPlaceId)));
	    array = JsonHelper.getJsonArrayFromUrlWithData(GET_SPOT_DETAIL_URL, placeData);
	    Place place = null;
	    try {
		// create a place
		place = new Place.Builder(
		// required parameters
		array.getJSONObject(0).getDouble("spots_tbl_longitude"), array.getJSONObject(0).getDouble("spots_tbl_latitude"), array.getJSONObject(0).getInt("spots_tbl_id"))
		// optional parameters
		.name(array.getJSONObject(0).getString("spots_tbl_name")).address(array.getJSONObject(0).getString("spots_tbl_description")).phoneNumber(array.getJSONObject(0).getString("spots_tbl_phone")).websiteUrl(array.getJSONObject(0).getString("spots_tbl_url")).build();

	    }
	    catch (JSONException e) {
		Log.e(TAG + "GetPlaceDetailTask.doInBackground(Void...voids) : ", "JSON error parsing data" + e.toString());
	    }
	    return place;
	}

	@Override
	protected void onPreExecute() {
	    // display waiting dialog
	    progressDialog = new ProgressDialog(PlaceInfoActivity.this);
	    progressDialog.setMessage("Loading ...");
	    progressDialog.setIndeterminate(true);
	    progressDialog.setCancelable(true);
	    progressDialog.show();
	}

	@Override
	protected void onPostExecute(final Place place) {
	    progressDialog.dismiss();

	    placeData = null;
	    array = null;
	    progressDialog = null;
	    System.gc();

	    TextView name = (TextView) findViewById(R.id.place_info_xml_textview_name);
	    name.setText(place.getName());

	    TextView description = (TextView) findViewById(R.id.place_info_xml_textview_description);
	    description.setText(place.getAddress());

	    TextView location = (TextView) findViewById(R.id.place_info_xml_textview_location);
	    location.setText("[" + Double.toString(place.getLatitude()) + ", " + Double.toString(place.getLongitude()) + "]");

	    TextView url = (TextView) findViewById(R.id.place_info_xml_textview_url);
	    if (url.equals("http://google.com") == false)
		url.setText("url:/" + place.getWebsiteUrl().substring(22));
	    else 
		url.setText("url:http//google.com");
	    
	    url.setClickable(true);
	    url.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    Bundle extras = new Bundle();
		    extras.putString("place_web_url", place.getWebsiteUrl());
		    Log.v(TAG, place.getWebsiteUrl());
		    Intent intent = new Intent(getApplicationContext(), WebviewActivity.class);
		    intent.putExtras(extras);
		    startActivity(intent);
		}
	    });

	    ImageView image = (ImageView) findViewById(R.id.place_info_xml_imageview_picture);
	    image.setImageResource(R.drawable.ic_launcher);

	    Button phoneButton = (Button) findViewById(R.id.place_info_xml_button_phone_number);
	    phoneButton.setText(place.getPhoneNumber());

	    final String phoneUrl = "tel:" + place.getPhoneNumber().replaceAll("-", "").replace("(", "").replace(")", "").replace(" ", "");
	    Log.v(TAG, phoneUrl);

	    phoneButton.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneUrl));
		    startActivity(intent);
		}
	    });

	    OverlayItem overlay = new OverlayItem(new GeoPoint((int) (place.getLatitude() * 1E6), (int) (place.getLongitude() * 1E6)), place.getName(), place.getAddress());
	    itemizedOverlay.addOverlay(overlay, place);
	    mapController.animateTo(new GeoPoint((int) (place.getLatitude() * 1E6), (int) (place.getLongitude() * 1E6)));
	    mapController.setZoom(16);
	}
    }

    @Override
    protected boolean isRouteDisplayed() {
	// TODO Auto-generated method stub
	return false;
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
	    finish();
	    break;
	case R.id.options_menu_xml_item_logout_icon:
	    SharedPreferences.Editor editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
	    editor.clear();
	    editor.commit();
	    intent = new Intent("com.csun.spotr.LoginActivity");
	    startActivity(intent);
	    finish();
	    break;
	case R.id.options_menu_xml_item_mainmenu_icon:
	    intent = new Intent("com.csun.spotr.MainMenuActivity");
	    startActivity(intent);
	    finish();
	    break;
	}
	return true;
    }

    @Override
    public void onPause() {
	Log.v(TAG, "I'm paused!");
	super.onPause();
    }

    @Override
    public void onDestroy() {
	Log.v(TAG, "I'm destroyed!");
	mapView = null;
	mapOverlays = null;
	mapController = null;
	itemizedOverlay = null;
	mapMarker = null;
	System.gc();
	super.onDestroy();
    }
}
