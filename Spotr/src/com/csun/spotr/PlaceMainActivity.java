package com.csun.spotr;

import com.csun.spotr.custom_gui.FlingableTabHost;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Framework for Spots.
 */
public class PlaceMainActivity extends TabActivity {
	private static final String TAG = "(PlaceMainActivity)";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.place_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
		
		Resources res = getResources(); 
		FlingableTabHost tabHost = (FlingableTabHost) getTabHost(); 
		FlingableTabHost.TabSpec spec; 
		Intent intent;

		// get place_id extras from PlaceActivity/LocalPlaceActivity
		Bundle extras = getIntent().getExtras();
		// create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, PlaceActionActivity.class);
		// pass this extra to PlaceActionActivity
		intent.putExtras(extras);

		// initialize a TabSpec for each tab and add it to the TabHost
	    View customTabView = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.tab_custom, null);
	    TextView tv = (TextView) customTabView.findViewById(R.id.tabText);
	    tv.setText("Missions");
		spec = tabHost
				.newTabSpec("missions")
				.setIndicator(customTabView)
				.setContent(intent);
		tabHost.addTab(spec);

		// do the same for the other tabs
		intent = new Intent().setClass(this, PlaceActivityActivity.class);
		// pass this Extra to PlaceActivityActivity
		intent.putExtras(extras);
		
		customTabView = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.tab_custom, null);
	    tv = (TextView) customTabView.findViewById(R.id.tabText);
	    tv.setText("News");
		spec = tabHost
				.newTabSpec("news")
				.setIndicator(customTabView)
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, PlaceInfoActivity.class);
		// pass this extra to PlaceInfoActivity
		intent.putExtras(extras);
		
		customTabView = LayoutInflater.from(tabHost.getContext()).inflate(R.layout.tab_custom, null);
	    tv = (TextView) customTabView.findViewById(R.id.tabText);
	    tv.setText("About");
		spec = tabHost
				.newTabSpec("about")
				.setIndicator(customTabView)
				.setContent(intent);
		tabHost.addTab(spec);
		// set current tab to action
		tabHost.setCurrentTab(0);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent("com.csun.spotr.MainMenuActivity");
		startActivity(intent);
		finish();
		return true;
	}
}
