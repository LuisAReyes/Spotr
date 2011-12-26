package com.csun.spotr;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class PlaceMainActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_main);
		Resources res = getResources(); 
		TabHost tabHost = getTabHost(); 
		TabHost.TabSpec spec; 
		Intent intent; 

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, PlaceActionActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("action")
				.setIndicator("Action", res.getDrawable(R.drawable.place_activity_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, PlaceActivityActivity.class);
		spec = tabHost
				.newTabSpec("activity")
				.setIndicator("Activity", res.getDrawable(R.drawable.place_activity_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, PlaceInfoActivity.class);
		spec = tabHost
				.newTabSpec("information")
				.setIndicator("Information", res.getDrawable(R.drawable.place_activity_tab))
				.setContent(intent);
		tabHost.addTab(spec);
		// set current tab to action
		tabHost.setCurrentTab(0);
	}
}
