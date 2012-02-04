package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.csun.spotr.core.adapter_item.QuestItem;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;

import com.csun.spotr.adapter.QuestItemAdapter;

public class QuestActivity extends Activity {
	private static final String TAG = "(QuestActivity)";
	private static final String GET_QUEST_URL = "http://107.22.209.62/android/get_quest.php";
	private ListView questListView;
	private QuestItemAdapter questItemAdapter;
	private List<QuestItem> questList = new ArrayList<QuestItem>();
	
	private TextView nameTextView;
	private TextView placeTextView;
	private TextView pointTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quest);
		questListView = (ListView) findViewById(R.id.quest_xml_listview_quest_list);
		questItemAdapter = new QuestItemAdapter(this, questList);
		questListView.setAdapter(questItemAdapter);
		
		//initialize detail description of specific quest
		nameTextView = (TextView) findViewById(R.id.quest_xml_textview_quest_name);
		placeTextView = (TextView) findViewById(R.id.quest_xml_textview_places);
		pointTextView = (TextView) findViewById(R.id.quest_xml_textview_rewards);
		
		//handle event when click on specific quest
		questListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				nameTextView.setText(questList.get(position).getName());
				placeTextView.setText(Integer.toString(questList.get(position).getSpotnum()));
				pointTextView.setText(Integer.toString(questList.get(position).getPoints()));

			}
		});
		
		new GetQuestTask().execute();
	}
	
	private class GetQuestTask extends AsyncTask<Integer, QuestItem, Boolean> {
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONArray userJsonArray = null;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(QuestActivity.this);
			progressDialog.setMessage("Loading quests in progress...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(QuestItem... quests) {
			questList.add(quests[0]);
			questItemAdapter.notifyDataSetChanged();
		}

		@Override
		protected Boolean doInBackground(Integer... offsets) {
			// send user id
			clientData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			// send offset
			// clientData.add(new BasicNameValuePair("offset", Integer.toString(offsets[0])));
			// retrieve data from server
			userJsonArray = JsonHelper.getJsonArrayFromUrlWithData(GET_QUEST_URL, clientData);
			if (userJsonArray != null) {
				try {
					for (int i = 0; i < userJsonArray.length(); ++i) {
						publishProgress(
							new QuestItem(
								userJsonArray.getJSONObject(i).getInt("quest_tbl_id"), 
								userJsonArray.getJSONObject(i).getString("quest_tbl_name"),
								userJsonArray.getJSONObject(i).getInt("quest_tbl_points"),
								userJsonArray.getJSONObject(i).getInt("quest_tbl_spotnum"),
								userJsonArray.getJSONObject(i).getString("quest_tbl_description"),
								userJsonArray.getJSONObject(i).getString("quest_tbl_url")));
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetQuestTask.doInBackGround(Integer... offsets) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onPause() {
		Log.v(TAG,"I'm paused");
		super.onPause();
	}
	
	
}