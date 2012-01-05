package com.csun.spotr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.csun.spotr.helper.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class SignupActivity extends Activity {
	private static final String TAG = "[SignupActivity]";
	private static final String SIGN_UP_URL = "http://107.22.209.62/android/signup.php";
	private Button 	buttonSignup = null;
	private EditText edittextEmail = null;
	private EditText edittextPassword = null;
	private EditText edittextConfirmPassword = null;
	private CheckBox checkboxVisible = null;
	private Button buttonExit = null;
	private boolean passwordVisible = false;
	private boolean validInformation = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		edittextEmail = (EditText) findViewById(R.id.signup_xml_edittext_email_id);
		edittextPassword = (EditText) findViewById(R.id.signup_xml_edittext_password_id);
		edittextConfirmPassword = (EditText) findViewById(R.id.signup_xml_edittext_confirmpassword_id);
		checkboxVisible = (CheckBox) findViewById(R.id.signup_xml_checkbox_visible_characters);
		buttonExit = (Button) findViewById(R.id.signup_xml_button_exit);
		buttonSignup = (Button) findViewById(R.id.signup_xml_button_signup);

		checkboxVisible.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (passwordVisible) {
					edittextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					edittextConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordVisible = false;
				}
				else {
					edittextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					edittextConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					passwordVisible = true;
				}
			}
		});

		buttonSignup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String email = edittextEmail.getText().toString();
				String password = edittextPassword.getText().toString();
				String confirmpassword = edittextConfirmPassword.getText().toString();
				if (!email.contains("@")) {
					showDialog(1);
				}
				else if (!password.equals(confirmpassword)) {
					showDialog(0);
				}
				else {
					SignupTask task = new SignupTask();
					task.execute();
				}
			}
		});
	}
	
	private class SignupTask extends AsyncTask<Void, Integer, Boolean> {
		private List<NameValuePair> datas = new ArrayList<NameValuePair>();
		@Override
		protected void onPreExecute() {
			datas.add(new BasicNameValuePair("username", edittextEmail.getText().toString().trim()));
			datas.add(new BasicNameValuePair("password", edittextPassword.getText().toString().trim()));
		}
		
		@Override
		protected Boolean doInBackground(Void... voids) {
			JSONObject json = JsonHelper.getJsonObjectFromUrlWithData(SIGN_UP_URL, datas);
			try {
				if (json.getString("result").equals("success"))
					return true;
			}
			catch (Exception e) {
				Log.e(TAG + "SignupTask.doInBackground(Void... voids)", "JSON error parsing data" + e.toString());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == false) {
				showDialog(3);
			}
			else {
				showDialog(2);
			}
		}
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0 :
			return 
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.error_circle)
					.setTitle("Error Message")
					.setMessage("Confirm password does not match.\n Please try again.")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					}).create();
			
		case 1 :
			return 
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.error_circle)
					.setTitle("Error Message")
					.setMessage("This is not a valid email address.\n Please try again.")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
						}
					}).create();
			
		case 2 :
			return 
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.bug)
					.setTitle("Congratz")
					.setMessage("Your account has been created. Log in and have fun playing SPOTR.")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							startActivity(new Intent("com.csun.spotr.LoginActivity"));
						}
					}).create();
			
		case 3 :
			return new AlertDialog.Builder(this)
				.setIcon(R.drawable.error_circle)
				.setTitle("Error Message")
				.setMessage("This email has been used.\n Please try again.")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				}).create();
		}
		return null;
	}
}
