package com.csun.spotr;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

public class FriendListActivity extends TabActivity {
	private static final String TAG = "[FriendListActivity]";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		Resources res = getResources(); 
		TabHost tabHost = getTabHost(); 
		TabHost.TabSpec spec; 
		Intent intent; 

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, FriendListMainActivity.class); 
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
}