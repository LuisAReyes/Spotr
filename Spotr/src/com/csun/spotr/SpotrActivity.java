package com.csun.spotr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SpotrActivity extends Activity {
	/** Called when the activity is first created. */
	private Button b1, b2, b3, b4, b5, b6, b7, b8, b9;
	
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
		
		b6 = (Button) findViewById(R.id.test_challengeinfo);
		b6.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.ChallengeInfoActivity");
				startActivity(i);
			}
		});
		
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
	}
}