package com.csun.spotr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * @author: Chan Nguyen
 */
public class ProfileEditActivity extends Activity {
	private static final String TAG = "[ProfileEditActivity]";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_edit);
	}
}