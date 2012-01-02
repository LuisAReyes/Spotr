package com.csun.spotr;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.core.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	private static final String 				TAG = "[MainMenuActivity]";
	private 			 String[] 				notificationList = null;
	private 			 ArrayAdapter<String> 	adapter = null;
	private 			 ListView 				mListView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu_original);

		// activities to begin
		Button btnProfile;
		Button btnChallenges;
		Button btnFriends;
		Button btnLeaderboards;
		Button btnRewards;
		Button btnSpots;
		Button btnSettings;
		Button btnLogoff;

		// button for the profile
		btnProfile = (Button) findViewById(R.id.main_menu_xml_button_profile_icon);
		btnProfile.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				User user = CurrentUser.getCurrentUser();
				Bundle extras = new Bundle();
				extras.putInt("user_id", user.getId());
				Intent intent = new Intent("com.csun.spotr.ProfileMainActivity");
				intent.putExtras(extras);
				startActivity(intent);
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

		btnLeaderboards = (Button) findViewById(R.id.main_menu_xml_button_leader_board_icon);
		btnLeaderboards.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
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
				Intent i = new Intent("com.csun.spotr.LocalPlaceActivity");
				startActivity(i);
			}
		});

		//populating Notification with Friends array
		notificationList = getResources().getStringArray(R.array.friends_array);
		mListView = (ListView) findViewById(R.id.main_menu_xml_slide_content);
				
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notificationList);
		mListView.setAdapter(adapter);

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
			Intent k = new Intent("com.csun.spotr.LoginActivity");
			startActivity(k);
			break;
		}
		return true;
	}
}