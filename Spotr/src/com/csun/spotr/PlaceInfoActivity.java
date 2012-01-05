package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.core.Place;
import com.csun.spotr.gui.BalloonItemizedOverlay;
import com.csun.spotr.helper.JsonHelper;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class PlaceInfoActivity extends MapActivity {
	private static final String TAG = "[PlaceInfoActivity]";
	private static final String GET_SPOT_DETAIL_URL = "http://107.22.209.62/android/get_spot_detail.php";
	private int currentPlaceId = 0;
	private MapView mapView = null;
	private List<Overlay> mapOverlays = null;
	private MapController mapController = null;
	private MyItemizedOverlay itemizedOverlay = null;
	
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
		Drawable drawble = getResources().getDrawable(R.drawable.map_maker_red);
		itemizedOverlay = new MyItemizedOverlay(drawble, mapView);
		mapOverlays.add(itemizedOverlay);
		
		// get place id
		Bundle extrasBundle = getIntent().getExtras();
		currentPlaceId = extrasBundle.getInt("place_id");
		
		new GetPlaceDetailTask().execute();
	}
	
	private class GetPlaceDetailTask extends AsyncTask<Void, Integer, Place> {
		private List<NameValuePair> placeData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

		@Override
		protected Place doInBackground(Void...voids) {
			placeData.add(new BasicNameValuePair("place_id", Integer.toString(currentPlaceId)));
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_SPOT_DETAIL_URL, placeData);
			Place place = null;
			try {
				// create a place
				place = new Place.Builder(
					// required parameters
					array.getJSONObject(0).getDouble("spots_tbl_longitude"), 
					array.getJSONObject(0).getDouble("spots_tbl_latitude"), 
					array.getJSONObject(0).getInt("spots_tbl_id"))
						// optional parameters
						.name(array.getJSONObject(0).getString("spots_tbl_name"))
						.address(array.getJSONObject(0).getString("spots_tbl_description"))
						.phoneNumber(array.getJSONObject(0).getString("spots_tbl_phone"))
							.build();
			}
			catch (JSONException e) {
				Log.e(TAG + ".filterPlaces() : ", "JSON error parsing data" + e.toString());
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
			
			TextView name = (TextView) findViewById(R.id.place_info_xml_textview_name);
			name.setText(place.getName());
			
			TextView description = (TextView) findViewById(R.id.place_info_xml_textview_description);
			description.setText(place.getAddress());
			
			TextView location = (TextView) findViewById(R.id.place_info_xml_textview_location);
			location.setText("[" + Double.toString(place.getLatitude()) + ", " + Double.toString(place.getLongitude()) + "]");
			
			ImageView image = (ImageView) findViewById(R.id.place_info_xml_imageview_picture);
			image.setImageResource(R.drawable.leopard_monitor);
			
			Button phone = (Button) findViewById(R.id.place_info_xml_button_phone_number);
			phone.setText("(" + place.getPhoneNumber().substring(0, 3) + ")-" + place.getPhoneNumber().substring(3, 6) + "-" + place.getPhoneNumber().substring(6));
			
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
}
