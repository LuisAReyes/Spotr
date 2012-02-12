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

import com.csun.spotr.core.adapter_item.QuestDetailItem;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;

import com.csun.spotr.adapter.QuestDetailItemAdapter;

public class QuestDetailActivity extends Activity {
	private static final String TAG = "(QuestDetailActivity)";
	private static final String GET_QUEST_DETAIL_URL = "http://107.22.209.62/android/get_quest_detail.php";
	private ListView questDetailListView;
	private QuestDetailItemAdapter questDetailItemAdapter;
	private List<QuestDetailItem> questDetailList = new ArrayList<QuestDetailItem>();
	private int quest_id=1;
	private int quest_points=0;
	private int quest_completed=0;
	
	private TextView playernameTextView;
	private TextView pointsTextView;
	private TextView challengedoneTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quest_detail);
		
		quest_id = this.getIntent().getExtras().getInt("quest_id");
		//quest_points = this.getIntent().getExtras().getInt("quest_points");
		
		questDetailListView = (ListView) findViewById(R.id.quest_detail_xml_listview_quest_list);
		questDetailItemAdapter = new QuestDetailItemAdapter(this.getApplicationContext(), questDetailList);
		questDetailListView.setAdapter(questDetailItemAdapter);
		
		//initialize detail description of specific quest
		playernameTextView = (TextView) findViewById(R.id.quest_detail_xml_textview_playername);
		pointsTextView = (TextView) findViewById(R.id.quest_detail_xml_textview_points);
		challengedoneTextView = (TextView) findViewById(R.id.quest_detail_xml_textview_challengedone);
		
		playernameTextView.setText(CurrentUser.getCurrentUser().getUsername());
				
		//handle event when click on specific quest
		questDetailListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent("com.csun.spotr.QuestActionActivity");
				Bundle extras = new Bundle();
				extras.putInt("place_id",questDetailList.get(position).getId() );
				intent.putExtras(extras);
				startActivity(intent);
				
			}
		});
		
		new GetQuestDetailTask().execute();
		
		//pointsTextView.setText(Integer.toString(quest_points));
		//challengedoneTextView.setText(Integer.toString(quest_completed) + "/" + Integer.toString(quest_points));

	}
	
	private class GetQuestDetailTask extends AsyncTask<Integer, QuestDetailItem, Boolean> {
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONArray userJsonArray = null;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(QuestDetailActivity.this);
			progressDialog.setMessage("Loading spots in progress...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(QuestDetailItem... spots) {
			questDetailList.add(spots[0]);
			questDetailItemAdapter.notifyDataSetChanged();
		}

		@Override
		protected Boolean doInBackground(Integer... offsets) {
			// send user id
			clientData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			 //send quest id
			 clientData.add(new BasicNameValuePair("quest_id", Integer.toString(quest_id)));
			// retrieve data from server
			userJsonArray = JsonHelper.getJsonArrayFromUrlWithData(GET_QUEST_DETAIL_URL, clientData);
			if (userJsonArray != null) {
				try {
					for (int i = 0; i < userJsonArray.length(); ++i) {
						publishProgress(
							new QuestDetailItem(
								userJsonArray.getJSONObject(i).getInt("spots_tbl_id"), 
								userJsonArray.getJSONObject(i).getString("spots_tbl_name"),
								userJsonArray.getJSONObject(i).getString("spots_tbl_description")
								));
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetQuestDetailTask.doInBackGround(Integer... offsets) : ", "JSON error parsing data" + e.toString());
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