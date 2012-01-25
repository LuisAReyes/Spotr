package com.csun.spotr;

import android.app.Activity;
import android.os.Bundle;

public class ProfileEditActivity extends Activity {
	private static final String TAG = "(ProfileEditActivity)";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_edit);
	}
}