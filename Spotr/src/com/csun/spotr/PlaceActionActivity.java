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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PlaceActionActivity extends Activity {
	private static final String TAG = "[PlaceActionActivity]";
	private static final String GET_CHALLENGES_URL = "http://107.22.209.62/android/get_challenges_from_place.php";
	private static final String DO_CHECK_IN_URL = "http://107.22.209.62/android/do_check_in.php";
	private int currentPlaceId;
	private int currentChosenItem;
	private ListView list = null;
	private	PlaceActionItemAdapter adapter = null;
	private List<Challenge> challengeList = new ArrayList<Challenge>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_action);
	
		// get place id
		Bundle extrasBundle = getIntent().getExtras();
		currentPlaceId = extrasBundle.getInt("place_id");
		
		// initialize list view of challenges
		list = (ListView) findViewById(R.id.place_action_xml_listview_actions);
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
					Intent intent = new Intent("com.csun.spotr.WriteOnWallActivity");
					Bundle extras = new Bundle();
					extras.putString("users_id", Integer.toString(CurrentUser.getCurrentUser().getId()));
					extras.putString("spots_id", Integer.toString(currentPlaceId));
					extras.putString("challenges_id", Integer.toString(c.getId()));
					intent.putExtras(extras);
					startActivity(intent);
				}
				else if (c.getType() == Challenge.Type.SNAP_PICTURE) {
					Intent intent = new Intent("com.csun.spotr.SnapPictureActivity");
					Bundle extras = new Bundle();
					extras.putString("users_id", Integer.toString(CurrentUser.getCurrentUser().getId()));
					extras.putString("spots_id", Integer.toString(currentPlaceId));
					extras.putString("challenges_id", Integer.toString(c.getId()));
					intent.putExtras(extras);
					startActivity(intent);
				}
				else if (c.getType() == Challenge.Type.QUESTION_ANSWER) {
					
				}
				else { // c.getType == Challenge.Type.OTHER 
				
				}
			}
		});
		
		// run GetChallengeTask
		new GetChallengesTask().execute();
	}
	
	private class GetChallengesTask extends AsyncTask<String, Challenge, Boolean> {
		private List<NameValuePair> challengeData = new ArrayList<NameValuePair>();
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
	    protected void onProgressUpdate(Challenge... challenges) {
			challengeList.add(challenges[0]);
			adapter.notifyDataSetChanged();
			// adapter.notifyDataSetInvalidated();
	    }

		@Override
		protected Boolean doInBackground(String... text) {
			challengeData.add(new BasicNameValuePair("place_id", Integer.toString(currentPlaceId)));
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_CHALLENGES_URL, challengeData);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) {
						publishProgress(
							new Challenge.Builder(
									// required parameters
									array.getJSONObject(i).getInt("challenges_tbl_id"), 
									Challenge.returnType(array.getJSONObject(i).getString("challenges_tbl_type")),
									array.getJSONObject(i).getInt("challenges_tbl_points")) 
										// optional parameters
										.name(array.getJSONObject(i).getString("challenges_tbl_name"))
										.description(array.getJSONObject(i).getString("challenges_tbl_description"))
											.build());
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetChallengesTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(PlaceActionActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("There are no challenges at this place!");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
		}
	}
	
	private class CheckInTask extends AsyncTask<String, Integer, String> {
		private List<NameValuePair> checkInData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActionActivity.this);
			progressDialog.setMessage("Checking in...");
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
			 * 3. All these complexity is done at server side, i.e. php script, so we only need to post to the server three parameters:
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
