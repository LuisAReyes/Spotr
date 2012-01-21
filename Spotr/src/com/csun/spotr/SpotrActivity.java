package com.csun.spotr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SpotrActivity extends Activity {
	private static final String TAG = "(SpotrActivity)";
	
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
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(this, "Back one more time to exits!", Toast.LENGTH_LONG);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}