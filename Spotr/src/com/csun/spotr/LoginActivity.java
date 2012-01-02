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
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private static final String TAG = "[LoginActivity]";
	private static final String LOGIN_URL = "http://107.22.209.62/android/login.php";
	private EditText edittextUsername;
	private EditText edittextPassword;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	private boolean prefsSavePassword = false;
	private boolean passwordVisible = false;
	private boolean savePassword = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				LoginTask task = new LoginTask();
				task.execute();
			}
		});
		
		buttonSignup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("com.csun.spotr.SignupActivity"));
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
		case 0 :
			return new 
				AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("Error Message")
					.setMessage("Invalid Username/Password.\n Please try again.")
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
	
	private class LoginTask extends AsyncTask<Void, Integer, Boolean> {
		private List<NameValuePair> loginData = new ArrayList<NameValuePair>();
		@Override
		protected void onPreExecute() {
			// make a pair of data for HttpPost 
			loginData.add(new BasicNameValuePair("username", edittextUsername.getText().toString()));
			loginData.add(new BasicNameValuePair("password", edittextPassword.getText().toString()));
		}
		
		@Override
		protected Boolean doInBackground(Void... unused) {
			// get data from the our database 
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(LOGIN_URL, loginData);
			try {
				JSONObject jsonUserInfo = array.getJSONObject(0);
				// attempt to get data
				jsonUserInfo.getString("username");
				if (savePassword) {
					editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
					editor.putBoolean("savePassword", true);
					editor.putString("username", jsonUserInfo.getString("username"));
					editor.putString("password", jsonUserInfo.getString("password"));
					editor.commit();
				}
				CurrentUser.setCurrentUser(jsonUserInfo.getInt("id"), jsonUserInfo.getString("username"), jsonUserInfo.getString("password"));
			}
			catch (Exception e) {
				showDialog(0);
				return false; 
			}
			return true;
		}

		protected void onProgressUpdate(Integer... progress) {
			// do nothing
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result == false) {
				showDialog(0);
			}
			else {
				startActivity(new Intent("com.csun.spotr.MainMenuActivity"));
			}
		}
	}

}