package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.CurrentUser;
import com.csun.spotr.core.PlaceActionItem;
import com.csun.spotr.core.User;
import com.csun.spotr.gui.FriendListMainItemAdapter;
import com.csun.spotr.gui.PlaceActionItemAdapter;
import com.csun.spotr.helper.JsonHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PlaceActionActivity extends Activity {
	private static final String TAG = "[PlaceActionActivity]";
	private static final String GET_CHALLENGES_URL = "http://107.22.209.62/android/get_challenges_from_place.php";
	private int currentPlaceId;
	private ListView list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_action);
	
		// initialize list view of challenges
		list = (ListView) findViewById(R.id.place_action_xml_listview_actions);
		
		// get place id
		Bundle extrasBundle = getIntent().getExtras();
		currentPlaceId = extrasBundle.getInt("place_id");
		
		GetChallengesTask task = new GetChallengesTask();
		task.execute();
	}
	
	private class GetChallengesTask extends AsyncTask<String, Integer, List<Challenge>> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActionActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected List<Challenge> doInBackground(String... text) {
			userData.add(new BasicNameValuePair("place_id", Integer.toString(currentPlaceId)));
			List<Challenge> challengeList = new ArrayList<Challenge>();
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_CHALLENGES_URL, userData);
			try {
				for (int i = 0; i < array.length(); ++i) {
					Challenge c = 
						new Challenge.Builder(
							array.getJSONObject(i).getInt("id"), 
							Challenge.returnType(array.getJSONObject(i).getString("type")),
							array.getJSONObject(i).getInt("points")) 
							.name(array.getJSONObject(i).getString("name"))
							.description(array.getJSONObject(i).getString("description"))
							.build();
					// add a challenge
					challengeList.add(c);
				}
			}
			catch (JSONException e) {
				Log.e(TAG + "GetChallengesTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return challengeList;
		}

		@Override
		protected void onPostExecute(List<Challenge> challengeList) {
			progressDialog.dismiss();
			PlaceActionItemAdapter adapter = new PlaceActionItemAdapter(PlaceActionActivity.this, challengeList);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				}
			});
		}
	}
	
	

}
