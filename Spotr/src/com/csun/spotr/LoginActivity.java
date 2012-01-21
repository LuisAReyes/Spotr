package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.core.User;
import com.csun.spotr.helper.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {
	private final String TAG = "[LoginActivity]";
	private final String LOGIN_URL = "http://107.22.209.62/android/login.php";
	private final int LOGIN_ERROR = 0;
	private final int CONNECTION_ERROR = 1;
	
	private EditText edittextUsername;
	private EditText edittextPassword;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	private boolean prefsSavePassword = false;
	private boolean passwordVisible = false;
	private boolean savePassword = false;
	private LoginTask task = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);

		prefs = getSharedPreferences("Spotr", MODE_PRIVATE);
		prefsSavePassword = prefs.getBoolean("savePassword", false);
		String prefsUsername = prefs.getString("username", "");
		String prefsPassword = prefs.getString("password", "");

		CheckBox checkVisible = (CheckBox) findViewById(R.id.login_xml_checkbox_visible_characters);
		CheckBox checkSavePassword = (CheckBox) findViewById(R.id.login_xml_checkbox_remember_password);
		edittextPassword = (EditText) findViewById(R.id.login_xml_edittext_password_id);
		edittextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		edittextUsername = (EditText) findViewById(R.id.login_xml_edittext_email_id);
		Button buttonLogin = (Button) findViewById(R.id.login_xml_button_login);
		Button buttonSignup = (Button) findViewById(R.id.login_xml_button_signup);
		
		// check Internet connection
		if (isNetworkAvailableAndConnected() == false) {
			showDialog(CONNECTION_ERROR);
			buttonLogin.setEnabled(false);
			buttonSignup.setEnabled(false);
		}
	
		// check saved password
		if (prefsSavePassword) {
			edittextUsername.append(prefsUsername);
			edittextPassword.append(prefsPassword);
		}

		checkVisible.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (passwordVisible) {
					edittextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordVisible = false;
				}
				else {
					edittextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					passwordVisible = true;
				}
			}
		});

		buttonLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				task = new LoginTask();
				task.execute();
			}
		});
		
		buttonSignup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("com.csun.spotr.SignupActivity"));
				finish();
			}
		});

		checkSavePassword.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!savePassword)
					savePassword = true;
				else
					savePassword = false;
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case LOGIN_ERROR :
			return new 
				AlertDialog.Builder(this)
					.setIcon(R.drawable.error_circle)
					.setTitle("Error Message")
					.setMessage("Invalid Username/Password.\n Please try again.")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
						}
					}).create();
		
		case CONNECTION_ERROR: 
			return new 
					AlertDialog.Builder(this)
						.setIcon(R.drawable.error_circle)
						.setTitle("Error network connection")
						.setMessage("Please turn on your network connection and try again!")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								
							}
						}).create();
		}
		return null;
	}

	public void onPause() {
		super.onPause();
		finish();
	}
	
	public class LoginTask extends AsyncTask<Void, Integer, Boolean> {
		private List<NameValuePair> loginData = new ArrayList<NameValuePair>();
		@Override
		protected void onPreExecute() {
			// make a pair of data for HttpPost 
			loginData.add(new BasicNameValuePair("username", edittextUsername.getText().toString().trim()));
			loginData.add(new BasicNameValuePair("password", edittextPassword.getText().toString().trim()));
		}
		
		@Override
		protected Boolean doInBackground(Void... voids) {
			// get data from the our database 
			JSONObject json = JsonHelper.getJsonObjectFromUrlWithData(LOGIN_URL, loginData);
			int userId;
			try {
				userId = json.getInt("result");
				// fail to login
				if (userId == -1)
					return false;
				
				if (savePassword) {
					editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
					editor.putBoolean("savePassword", true);
					editor.putString("username", edittextUsername.getText().toString());
					editor.putString("password", edittextPassword.getText().toString());
					editor.commit();
				}
				// set current user
				CurrentUser.setCurrentUser(
					userId, 
					edittextUsername.getText().toString(), 
					edittextPassword.getText().toString());
			}
			catch (Exception e) {
				Log.e(TAG + "LoginTask.doInBackground(Void... voids)", "JSON error parsing data" + e.toString());
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == false) {
				showDialog(LOGIN_ERROR);
			}
			else {
				startActivity(new Intent("com.csun.spotr.MainMenuActivity"));
				finish();
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        startActivity(new Intent(getApplicationContext(), SpotrActivity.class));
	        finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	public LoginTask getLoginTask() {
		return task;
	}
	
	private boolean isNetworkAvailableAndConnected() {
		ConnectivityManager conManager =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}
		else if (!networkInfo.isConnected()) {
			return false;
		}
		else if (!networkInfo.isAvailable()) {
			return false;
		}
		return true;
	}
}