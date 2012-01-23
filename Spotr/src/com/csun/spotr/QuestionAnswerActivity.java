package com.csun.spotr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.singleton.CurrentDateTime;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.Base64;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.util.UploadFileHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionAnswerActivity extends Activity {
	private static final String TAG = "(SnapPictureActivity)";
	private static final String QUESTION_ANSWER_URL = "http://107.22.209.62/android/do_question_answer.php";
	private TextView textViewQuestion = null;
	private EditText editTextAnswer = null; 
	private Button buttonSubmit = null;
	private JSONObject json = null;
	private QuestionAnswerTask task = null;
	private String usersId;
	private String spotsId;
	private String challengesId;
	private String challengeQuestion;
	private String userAnswer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_answer);
		
		Bundle extras = getIntent().getExtras();
		usersId = extras.getString("users_id");
		spotsId = extras.getString("spots_id");
		challengesId = extras.getString("challenges_id");
		challengeQuestion = extras.getString("question_description");
	
		textViewQuestion = (TextView) findViewById(R.id.question_answer_xml_textview_question);
		textViewQuestion.setText(challengeQuestion);
		
		editTextAnswer = (EditText) findViewById(R.id.question_answer_xml_edittext_answer);
		buttonSubmit = (Button) findViewById(R.id.question_answer_xml_button_submit);
		buttonSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				userAnswer = editTextAnswer.getText().toString().trim();
				if (userAnswer.length() > 0) {
					task = new QuestionAnswerTask();
					task.execute();
				}
				else {
					displayErrorMessage();
				}
			}
		});
	}

	private class QuestionAnswerTask extends AsyncTask<Void, Integer, String> {
		ProgressDialog progressDialog = new ProgressDialog(QuestionAnswerActivity.this);
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(QuestionAnswerActivity.this);
			progressDialog.setMessage("Uploading question...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			clientData.add(new BasicNameValuePair("users_id", usersId));
			clientData.add(new BasicNameValuePair("spots_id", spotsId));
			clientData.add(new BasicNameValuePair("challenges_id", challengesId));
			clientData.add(new BasicNameValuePair("user_answer", userAnswer));
			json = JsonHelper.getJsonObjectFromUrlWithData(QUESTION_ANSWER_URL, clientData);
			String result = "";
			try {
				result = json.getString("result");
			} 
			catch (JSONException e) {
				Log.e(TAG + "QuestionAnswerTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
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
	
	private void displayErrorMessage() {
		AlertDialog dialogMessage = new AlertDialog.Builder(QuestionAnswerActivity.this).create();
		dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
		dialogMessage.setMessage("Answer cannot be empty! Please try again.");
		dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialogMessage.show();	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_menu, menu);
		return true;
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG, "I'm destroyed!");
		super.onDestroy();
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
				finish();
				break;
			case R.id.options_menu_xml_item_mainmenu_icon:
				intent = new Intent("com.csun.spotr.MainMenuActivity");
				startActivity(intent);
				break;
		}
		return true;
	}
}
