package com.csun.spotr;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TabHost;

public class FriendListMainActivity extends TabActivity {
	private static final String TAG = "(FriendListMainActivity)";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		
		Resources res = getResources(); 
		TabHost tabHost = getTabHost(); 
		TabHost.TabSpec spec; 
		Intent intent; 

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, FriendListActivity.class); 
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("All Friends")
				.setIndicator("All Friends", res.getDrawable(R.drawable.place_activity_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, FriendListActionActivity.class);
		spec = tabHost
				.newTabSpec("Find")
				.setIndicator("Find", res.getDrawable(R.drawable.place_activity_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, FriendListFeedActivity.class);
		spec = tabHost
				.newTabSpec("Friend Feeds")
				.setIndicator("Friend Feeds", res.getDrawable(R.drawable.place_activity_tab))
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
}