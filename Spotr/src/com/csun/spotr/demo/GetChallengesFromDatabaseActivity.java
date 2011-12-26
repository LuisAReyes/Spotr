package com.csun.spotr.demo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.R;
import com.csun.spotr.helper.CustomHttpClient;
import com.csun.spotr.helper.JsonHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GetChallengesFromDatabaseActivity extends Activity {
	private final String TAG = "[GetChallengesFromDatabaseActivity]";
	private final String url = "http://107.22.209.62/android/retrieve_challenge.php";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.empty);
		GetChallengeTask task = new GetChallengeTask();
		task.execute();
	}

	private class GetChallengeTask extends AsyncTask<Void, Integer, String> {
		private  ProgressDialog progressDialog = null;
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(GetChallengesFromDatabaseActivity.this);
			progressDialog.setMessage("Loading Challenges...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(Void... unused) {
			return getChallengeFromDatabase(url);
		}

		protected void onProgressUpdate(Integer... progress) {
			//
		}
		
		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (result != null) {
				TextView tv = new TextView(GetChallengesFromDatabaseActivity.this);
				tv.setText(result);
				GetChallengesFromDatabaseActivity.this.setContentView(tv);
			}
		}

		public String getChallengeFromDatabase(String... urls) { 
			JSONArray array = JsonHelper.getJsonArrayFromUrl(url);
			StringBuilder result = new StringBuilder();
			try {
				for (int i = 0; i < array.length(); ++i) { 
					JSONObject temp = array.getJSONObject(i);
					result.append(temp.getString("id"));
					result.append(temp.getString("spots_id"));
					result.append(temp.getString("name"));
					result.append(temp.getString("description"));
					result.append(temp.getString("is_quest"));
					result.append(temp.getString("rating"));
					result.append(temp.getString("points"));
					result.append("\n");
				}
			}
			catch (JSONException e) {
				Log.e(TAG + ".getChallengeFromDatabase() : ", "JSON error parsing data" + e.toString());
			}
			return result.toString();
		}
	}
}
