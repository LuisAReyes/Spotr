package com.csun.spotr.task;

/*
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.csun.spotr.R;
import com.csun.spotr.gui.LocationItemAdapter;
import com.csun.spotr.helper.DownloadImageHelper;
import com.csun.spotr.helper.GooglePlaceHelper;
import com.csun.spotr.helper.JsonHelper;
import com.csun.spotr.helper.MyLocation;
import com.csun.spotr.helper.MyLocation.LocationResult;

public class UpdatePlaceActivityTask extends AsyncTask<Context, Void, Location> {
	private Context context;
	int progress = -1;
	MyLocation currentLocation = null;

	public void setContext(Context c) {
		context = c;
		if (progress >= 0) {
			publishProgress(this.progress);
		}
	}

	protected void onPreExecute() {
		progress = 0;
	}

	@Override
	protected Location doInBackground(Integer... params) {
		MyLocation myLoc = new MyLocation();
		myLoc.getLocation(context, locationResult);
		return null;
	}

	public LocationResult locationResult = new LocationResult() {
		@Override
		public void gotLocation(final Location location) {
			onPostExecute(location);
		}
	};

	protected void onPostExecute(Location location) {
		String url = GooglePlaceHelper.buildGooglePlaceUrl(location, "1000");
		JSONObject json = JsonHelper.getJSONfromURL(url);

		String[] names = null;
		String[] categories = null;
		String[] ratings = null;
		Bitmap[] iconBitmaps = null;
		try {
			JSONArray placeInformationArray = json.getJSONArray("results");

			names = new String[placeInformationArray.length()];
			categories = new String[placeInformationArray.length()];
			ratings = new String[placeInformationArray.length()];
			iconBitmaps = new Bitmap[placeInformationArray.length()];

			for (int i = 0; i < placeInformationArray.length(); i++) {
				JSONObject jsonObject = placeInformationArray.getJSONObject(i);
				names[i] = jsonObject.getString("name");
				categories[i] = jsonObject.getString("types");
				ratings[i] = "5.0";
				iconBitmaps[i] = DownloadImageHelper.downloadImage(jsonObject.getString("icon"));
			}
		}
		catch (JSONException e) {
			Log.e("[PlaceActivity] run()", "JSON error parsing data " + e.toString());
		}

		ListView locationsListView = (ListView) findViewById(R.id.place_xml_listview_places);
		LocationItemAdapter itemAdapter = new LocationItemAdapter((Activity) context, names, iconBitmaps, categories, ratings);
		// set filter
		locationsListView.setTextFilterEnabled(true);

		// search text
		EditText searchEditText = (EditText) findViewById(R.id.place_xml_edittext_search);
		searchEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			public void afterTextChanged(Editable arg0) {
			}
		});
		// set adapter
		locationsListView.setAdapter(itemAdapter);
		locationsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});

	}
}
*/