package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.core.Challenge;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.gui.PlaceActionItemAdapter;
import com.csun.spotr.helper.JsonHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PlaceActionActivity extends Activity {
	private static final  String TAG = "[PlaceActionActivity]";
	private static final  String GET_CHALLENGES_URL = "http://107.22.209.62/android/get_challenges_from_place.php";
	private static final  String DO_CHECK_IN_URL = "http://107.22.209.62/android/do_check_in.php";
	private int currentPlaceId;
	private int currentChosenItem;
	private ListView list;
	private	PlaceActionItemAdapter  adapter;
	
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
		// create GetChallengeTask
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
		protected void onPostExecute(final List<Challenge> challengeList) {
			progressDialog.dismiss();
			adapter = new PlaceActionItemAdapter(PlaceActionActivity.this, challengeList);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Challenge c = challengeList.get(position);
					// set current item chosen so that later we can make some side effects
					currentChosenItem = position;
					
					if (c.getType() == Challenge.Type.CHECK_IN) {
						CheckInTask task = new CheckInTask();
						task.execute(
							Integer.toString(CurrentUser.getCurrentUser().getId()),
							Integer.toString(currentPlaceId),
							Integer.toString(c.getId())
						);
					}
					else if (c.getType() == Challenge.Type.WRITE_ON_WALL) {
						
					}
					else if (c.getType() == Challenge.Type.SNAP_PICTURE) {
						
					}
					else if (c.getType() == Challenge.Type.QUESTION_ANSWER) {
						
					}
					else {// c.getType == Challenge.Type.OTHER 
					
					}
				}
			});
		}
		
		private class CheckInTask extends AsyncTask<String, Integer, String> {
			private List<NameValuePair> checkInData = new ArrayList<NameValuePair>();
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
			protected String doInBackground(String... ids) {
				/*
				 * 1. Retrieve data from [activity] table where $users_id and $places_id
				 * 2. Check the result of this query:
				 * 		a. If the result is null, then user hasn't visited this place yet which also implies that he has not done any challenges.
				 * 		   Thus we can update the current user:
				 * 		   i.  Update [activity] table with $users_id, $places_id, $challenges_id   
				 * 		   ii. Update [users] table with 
				 * 			   + $challenges_done = $challenges_done + 1
				 * 			   + $points += challenges.points
				 * 			   + $places_visited = $places_visited + 1
				 * 		b. If the result is not null, update [activity] table with $users_id, $places_id, $challenges_id with CURRENT_TIMESTAMP, but
				 * 		   don't run the statement:
				 * 		       + $places_visited = $places_visited + 1
				 * 3. All these complexity is done at server side, i.e. php script, so we only need to post in three parameters:
				 * 	    a. users_id
				 * 		b. places_id
				 * 		c. challenges_id
				 * 4. The return of this query is the number points is added the points added to the user account.
				 */
				checkInData.add(new BasicNameValuePair("users_id", ids[0]));
				checkInData.add(new BasicNameValuePair("spots_id", ids[1]));
				checkInData.add(new BasicNameValuePair("challenges_id", ids[2]));
				JSONObject json = JsonHelper.getJsonObjectFromUrlWithData(DO_CHECK_IN_URL, checkInData);
				String result = "";
				try {
					result = json.getString("result");
				} 
				catch (JSONException e) {
					Log.e(TAG + "CheckInTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				progressDialog.dismiss();
				if (result.equals("success")) {
					progressDialog.dismiss();
					list.getChildAt(currentChosenItem).setBackgroundColor(Color.GRAY);
				}
			}
		}
		
	}
	
	

}
