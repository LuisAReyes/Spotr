package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.helper.JsonHelper;
import com.csun.spotr.singleton.CurrentUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WriteOnWallActivity extends Activity {
	private static final String TAG = "[WriteOnWallActivity]";
	private static final String WRITE_ON_WALL_URL = "http://107.22.209.62/android/do_write_on_wall.php";
	private Button buttonPost;
	private EditText editTextMessage;
	private String usersId;
	private String spotsId;
	private String challengesId;
	private String message;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_on_wall);
		
		buttonPost = (Button) findViewById(R.id.write_on_wall_xml_button_submit);
		editTextMessage = (EditText) findViewById(R.id.write_on_wall_xml_edittext_message_box);
		
		Bundle extras = getIntent().getExtras();
		usersId = extras.getString("users_id");
		spotsId = extras.getString("spots_id");
		challengesId = extras.getString("challenges_id");
		
		buttonPost.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				message = editTextMessage.getText().toString();
				if (message.length() > 0) {
					WriteOnWallTask task = new WriteOnWallTask();
					task.execute();
				}
				else {
					displayErrorMessage();
				}
			}
		});
	}
	
	private void displayErrorMessage() {
		AlertDialog dialogMessage = new AlertDialog.Builder(WriteOnWallActivity.this).create();
		dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
		dialogMessage.setMessage("Message cannot be empty! Please try again.");
		dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogMessage.show();	
	}
	
	private class WriteOnWallTask extends AsyncTask<Void, Integer, String> {
		ProgressDialog progressDialog = new ProgressDialog(WriteOnWallActivity.this);
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(WriteOnWallActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... voids) {
			clientData.add(new BasicNameValuePair("users_id", usersId));
			clientData.add(new BasicNameValuePair("spots_id", spotsId));
			clientData.add(new BasicNameValuePair("challenges_id", challengesId));
			clientData.add(new BasicNameValuePair("comment", message));
			JSONObject json = JsonHelper.getJsonObjectFromUrlWithData(WRITE_ON_WALL_URL, clientData);
			String result = "";
			try {
				result = json.getString("result");
			} 
			catch (JSONException e) {
				Log.e(TAG + "UploadMessageTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (result.equals("success")) {
				Intent intent = new Intent("com.csun.spotr.PlaceMainActivity");
				intent.putExtra("place_id", Integer.parseInt(spotsId));
				startActivity(intent);
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.options_menu_xml_item_setting_icon:
				intent = new Intent("com.csun.spotr.SettingsActivity");
				startActivity(intent);
				break;
			case R.id.options_menu_xml_item_logout_icon:
				SharedPreferences.Editor editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
				editor.clear();
				editor.commit();
				intent = new Intent("com.csun.spotr.LoginActivity");
				startActivity(intent);
				break;
			case R.id.options_menu_xml_item_mainmenu_icon:
				intent = new Intent("com.csun.spotr.MainMenuActivity");
				startActivity(intent);
				break;
		}
		return true;
	}
}
