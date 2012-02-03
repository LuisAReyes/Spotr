package com.csun.spotr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CreateLostItemActivity extends Activity {
	private static final String TAG = "(CreateLostItemActivity)";
	private static final String GET_USER_POINTS_URL = "http://107.22.209.62/android/get_user_points.php";
	private static Integer userPoints = 0;
	private EditText editTextName;
	private EditText editTextDescription;
	private EditText editTextPoints;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_lost_item);
		
		// hide keyboard right away
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		editTextName = (EditText) findViewById(R.id.create_lost_item_xml_edittext_name);
		editTextDescription = (EditText) findViewById(R.id.create_lost_item_xml_edittext_description);
		editTextPoints = (EditText) findViewById(R.id.create_lost_item_xml_edittext_points);
		
		imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editTextDescription.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editTextPoints.getWindowToken(), 0);
		
		Log.d(TAG, "currentUser: " + CurrentUser.getCurrentUser().getId());
		
		// get user's current points
		new GetUserPoints(this, CurrentUser.getCurrentUser().getId()).execute();
	}
	
	// We need the user's points to determine how many he/she can distribute
	private static class GetUserPoints extends AsyncTask<Void, Integer, Boolean> {
		private WeakReference<CreateLostItemActivity> refActivity;
		private List<NameValuePair> jsonList = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private int userId;
		
		public GetUserPoints(Activity c, int id) {
			refActivity = new WeakReference<CreateLostItemActivity>((CreateLostItemActivity)c);
			userId = id;
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(refActivity.get());
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			jsonList.add(new BasicNameValuePair("id", Integer.toString(userId)));
			JSONArray jsonArray = JsonHelper.getJsonArrayFromUrlWithData(GET_USER_POINTS_URL, jsonList);
			if(jsonArray != null) {
				try {
					if(jsonArray.getJSONObject(0).getString("users_tbl_points") != null) {
						userPoints = jsonArray.getJSONObject(0).getInt("users_tbl_points");
						Log.d(TAG, "userPoints: " + userPoints);
					}
				} catch (JSONException e) {
					Log.e(TAG + ": GetUserPoints()", e.toString());
				}
				
				return true;
			}
			
			return false;
		}
		
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(refActivity.get()).create();
				dialogMessage.setTitle("Oops!");
				dialogMessage.setMessage("Sorry " + CurrentUser.getCurrentUser().toString() + ", it seems there was an error.");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
		}
	

	}
	
	@Override
	public void onPause() {
		Log.v(TAG, "I'm paused!");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "I'm destroyed!");
		super.onDestroy();
	}
}
