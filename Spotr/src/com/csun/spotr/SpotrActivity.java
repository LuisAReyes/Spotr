package com.csun.spotr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author: Chan Nguyen
 */
public class SpotrActivity extends Activity {
	// testing 
	private Button b1;
	private Button b2;
	private Button b3;
	private Button b4;
	private Button b5;
	private Button b6;
	private Button b7;
	private Button b8;
	private Button b9;
	private Button b10;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		b1 = (Button) findViewById(R.id.test_login);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.LoginActivity");
				startActivity(i);
			}
		});

		b2 = (Button) findViewById(R.id.test_mainmenu);
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.MainMenuActivity");
				startActivity(i);
			}
		});

		b3 = (Button) findViewById(R.id.test_challenge);
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.ChallengeActivity");
				startActivity(i);
			}
		});

		b4 = (Button) findViewById(R.id.test_mapview);
		b4.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.MapViewActivity");
				startActivity(i);
			}
		});
		
		b5 = (Button) findViewById(R.id.test_reward);
		b5.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.RewardActivity");
				startActivity(i);
			}
		});
		
		/*b6 = (Button) findViewById(R.id.test_challengeinfo);
		b6.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.ChallengeInfoActivity");
				startActivity(i);
			}
		});*/
		
		b7 = (Button) findViewById(R.id.test_profile);
		b7.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.ProfileActivity");
				startActivity(i);
			}
		});
		
		b8 = (Button) findViewById(R.id.test_friendlist);
		b8.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.FriendListActivity");
				startActivity(i);
			}
		});
		
		b9 = (Button) findViewById(R.id.test_place);
		b9.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.PlaceActivity");
				startActivity(i);
			}
		});
		
		b10 = (Button) findViewById(R.id.test_leaderboard);
		b10.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.LeaderboardActivity");
				startActivity(i);
			}
		});
	}
}