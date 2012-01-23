package com.csun.spotr;

import android.app.Activity;
import android.os.Bundle;

public class ProfilePreferenceActivity extends Activity {
	private static final String TAG = "(ProfilePreferenceActivity)";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_preference);
	}
}