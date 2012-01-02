package com.csun.spotr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Chan Nguyen 
 */
public class PlaceDetailActivity extends Activity {
	private static final String TAG = "[PlaceDetailActivity]";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_detail_info);
		// get information from MapIconActivity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			// set up widgets
			TextView nameTextView = (TextView) findViewById(R.id.place_detail_info_xml_textview_name);
			TextView vicinityTextView = (TextView) findViewById(R.id.place_detail_info_xml_textview_vicinity);
			TextView categoryTextView = (TextView) findViewById(R.id.place_detail_info_xml_textview_category);
			TextView coordinateTextView = (TextView) findViewById(R.id.place_detail_info_xml_coordinate);
			// assign data
			nameTextView.setText(extras.getString("place_name"));
			vicinityTextView.setText(extras.getString("place_vicinity"));
			categoryTextView.setText(extras.getString("place_category"));
			coordinateTextView.setText(extras.getString("place_coordinate"));
		}
	}
}
