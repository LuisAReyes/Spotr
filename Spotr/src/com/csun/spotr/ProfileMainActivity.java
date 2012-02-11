package com.csun.spotr;

import com.csun.spotr.custom_gui.FlingableTabHost;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.widget.TabHost;

public class ProfileMainActivity extends TabActivity {
	private final static String TAG = "(ProfileMainActivity)";
	
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
		
		// get use_id from ProfileMainActivity
		Bundle extras = getIntent().getExtras();
		int currentUserId = extras.getInt("user_id");
		// tab 1
		intent = new Intent().setClass(this, ProfileActivity.class);
		// pass it to ProfileActivity
		intent.putExtra("user_id", currentUserId);
		
		spec = tabHost
				.newTabSpec("profile")
				.setIndicator("Profile", res.getDrawable(R.drawable.place_activity_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		// tab 2
		intent = new Intent().setClass(this, LeaderboardActivity.class);
		spec = tabHost
				.newTabSpec("leaderboard")
				.setIndicator("Leaderboard", res.getDrawable(R.drawable.place_activity_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		// tab 3
		intent = new Intent().setClass(this, RewardActivity.class);
		spec = tabHost
				.newTabSpec("reward")
				.setIndicator("Rewards", res.getDrawable(R.drawable.place_activity_tab))
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
