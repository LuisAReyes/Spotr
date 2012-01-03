package com.csun.spotr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SpotrActivity extends Activity {
	private static final String TAG = "[SpotrActivity]";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button b1 = (Button) findViewById(R.id.test_login);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.LoginActivity");
				startActivity(i);
			}
		});

		Button b2 = (Button) findViewById(R.id.test_challenge);
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.ChallengeActivity");
				startActivity(i);
			}
		});

		Button b3 = (Button) findViewById(R.id.test_mapview);
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.MapViewActivity");
				startActivity(i);
			}
		});
		
		Button b4 = (Button) findViewById(R.id.test_place);
		b4.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.PlaceActivity");
				startActivity(i);
			}
		});
		
		Button b5 = (Button) findViewById(R.id.test_localmapview);
		b5 .setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.LocalMapViewActivity");
				startActivity(i);
			}
		});
	
		Button b6 = (Button) findViewById(R.id.test_localplace);
		b6.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.LocalPlaceActivity");
				startActivity(i);
			}
		});
	}
}