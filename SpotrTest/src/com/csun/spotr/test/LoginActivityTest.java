package com.csun.spotr.test;

import com.csun.spotr.LoginActivity;
import com.csun.spotr.singleton.CurrentUser;

import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {
	private LoginActivity activity;
	private EditText editTextUsername;
	private EditText editTextPassword;
	private Button buttonLogin;

	public LoginActivityTest() {
		super("com.csun.spotr.LoginActivity", LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		editTextUsername = (EditText) activity.findViewById(com.csun.spotr.R.id.login_xml_edittext_email_id);
		editTextPassword = (EditText) activity.findViewById(com.csun.spotr.R.id.login_xml_edittext_password_id);
		buttonLogin = (Button) activity.findViewById(com.csun.spotr.R.id.login_xml_button_login);
	}

	public void testLoginSuccess() throws Throwable {
		runTestOnUiThread(new Runnable() {
			public void run() {
				editTextUsername.setText("chan");
				editTextPassword.setText("123");
				buttonLogin.performClick();
			}
		});

		// if AsyncTask complete
		assertTrue(activity.getLoginTask().get());
		assertEquals(CurrentUser.getCurrentUser().getUsername().equals("chan") && CurrentUser.getCurrentUser().getPassword().equals("123"), true);
	}
}
