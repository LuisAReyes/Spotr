package com.csun.spotr;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class ProfileMainActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_main);
		Resources res = getResources(); 
		TabHost tabHost = getTabHost(); 
		TabHost.TabSpec spec; 
		Intent intent; 

		// tab 1
		intent = new Intent().setClass(this, ProfileActivity.class);
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
}
