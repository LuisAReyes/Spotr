package com.csun.spotr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);

		// activities to begin
		Button btnProfile, btnChallenges, btnFriends, btnLeaderboards, btnRewards, btnSpots, btnSettings, btnLogoff;

		// button for the profile
		btnProfile = (Button) findViewById(R.id.main_menu_xml_button_profile_icon);
		btnProfile.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.ProfileActivity");
				startActivity(i);
			}
		});

		// button for the challenge
		btnChallenges = (Button) findViewById(R.id.main_menu_xml_button_challenge_icon);
		btnChallenges.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.ChallengeActivity");
				startActivity(i);
			}
		});

		// button for the friends list
		btnFriends = (Button) findViewById(R.id.main_menu_xml_button_friend_icon);
		btnFriends.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.FriendListActivity");
				startActivity(i);
			}
		});

		// button for the leaderboard 	
		btnLeaderboards = (Button) findViewById(R.id.main_menu_xml_button_leader_board_icon);
		btnLeaderboards.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// editted by Chan Nguyen 11/12/2011
				Intent i = new Intent("com.csun.spotr.LeaderboardActivity");
				startActivity(i);
			}
		});

		// button for rewards
		btnRewards = (Button) findViewById(R.id.main_menu_xml_button_award_icon);
		btnRewards.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.RewardActivity");
				startActivity(i);
			}
		});

		// button for spots
		btnSpots = (Button) findViewById(R.id.main_menu_xml_button_spot_icon);
		btnSpots.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.MapViewActivity");
				startActivity(i);
			}
		});

		/*
		 * btnSettings = (Button)
		 * findViewById(R.id.main_menu_xml_button_spot_icon);
		 * btnSettings.setOnClickListener(new OnClickListener() { public void
		 * onClick(View arg0) { Intent i = new
		 * Intent("com.csun.spotr.settingsActivity"); startActivity(i); } });
		 * 
		 * btnLogoff = (Button)
		 * findViewById(R.id.main_menu_xml_button_spot_icon);
		 * btnLogoff.setOnClickListener(new OnClickListener() { public void
		 * onClick(View arg0) { Intent i = new
		 * Intent("com.csun.spotr.ProfileActivity"); startActivity(i); } });
		 */

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.options_menu_xml_item_setting_Icon :
			Intent i = new Intent("com.csun.spotr.SettingsActivity");
			startActivity(i);
			break;
		case R.id.options_menu_xml_item_logout_Icon :
			break;
		}
		return true;
	}
}