package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.FriendFeed;
import com.csun.spotr.core.PlaceLog;
import com.csun.spotr.gui.FriendFeedItemAdapter;
import com.csun.spotr.gui.PlaceActivityItemAdapter;
import com.csun.spotr.gui.UserActivityItemAdapter;
import com.csun.spotr.helper.DownloadImageHelper;
import com.csun.spotr.helper.JsonHelper;
import com.csun.spotr.singleton.CurrentUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendListFeedActivity extends Activity {
	private static final String TAG = "[FriendListFeedActivity]";
	private static final String GET_FRIEND_FEED_URL = "http://107.22.209.62/android/get_friend_feeds.php";
	private List<FriendFeed> friendFeedList = new ArrayList<FriendFeed>();
	private ListView list;
	private FriendFeedItemAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_feed);
		
		list = (ListView) findViewById(R.id.friend_list_feed_xml_listview);
		adapter = new FriendFeedItemAdapter(FriendListFeedActivity.this, friendFeedList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
		new GetFriendFeedTask().execute();
    }
    
    private class GetFriendFeedTask extends AsyncTask<Void, FriendFeed, Boolean> {
		private List<NameValuePair> datas = new ArrayList<NameValuePair>(); 
		
		@Override
		protected void onPreExecute() {
			datas.add(new BasicNameValuePair("users_id", Integer.toString(CurrentUser.getCurrentUser().getId())));
		}
		
		@Override
		  protected void onProgressUpdate(FriendFeed... feeds) {
			friendFeedList.add(feeds[0]);
			adapter.notifyDataSetChanged();
			// adapter.notifyDataSetInvalidated();
	    }
		
		@Override
		protected Boolean doInBackground(Void...voids) {
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_FRIEND_FEED_URL, datas);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) { 
						publishProgress(
							new FriendFeed.Builder(
									// required parameters
									array.getJSONObject(i).getInt("activity_tbl_id"),
									array.getJSONObject(i).getInt("friends_tbl_friend_id"),
									array.getJSONObject(i).getString("users_tbl_username"),
									Challenge.returnType(array.getJSONObject(i).getString("challenges_tbl_type")),
									array.getJSONObject(i).getString("activity_tbl_created"),
									array.getJSONObject(i).getString("spots_tbl_name"))
										// optional parameters
										.challengeName(array.getJSONObject(i).getString("challenges_tbl_name"))
										.challengeDescription(array.getJSONObject(i).getString("challenges_tbl_description"))
										.activitySnapPictureDrawable(DownloadImageHelper.getImageFromUrl(array.getJSONObject(i).getString("activity_tbl_snap_picture_url")))
										.friendPictureDrawable(DownloadImageHelper.getImageFromUrl(array.getJSONObject(i).getString("users_tbl_user_image_url")))
										.activityComment(array.getJSONObject(i).getString("activity_tbl_comment"))
											.build());
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFriendFeedTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(FriendListFeedActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("There are no friend feeds yet!");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
		}
			
	}

}