package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.adapter.FriendFeedItemAdapter;
import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.adapter_item.FriendFeedItem;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendListFeedActivity extends Activity {
	private static final String TAG = "(FriendListFeedActivity)";
	private static final String GET_FRIEND_FEED_URL = "http://107.22.209.62/android/get_friend_feeds.php";
	private List<FriendFeedItem> friendFeedList = new ArrayList<FriendFeedItem>();
	private ListView listview = null;
	private FriendFeedItemAdapter adapter = null;
	private GetFriendFeedTask task = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_feed);
		
		listview = (ListView) findViewById(R.id.friend_list_feed_xml_listview);
		adapter = new FriendFeedItemAdapter(this.getApplicationContext(), friendFeedList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
		task = new GetFriendFeedTask();
		task.execute();
    }
    
    private class GetFriendFeedTask extends AsyncTask<Void, FriendFeedItem, Boolean> {
		private List<NameValuePair> datas = new ArrayList<NameValuePair>(); 
		private ProgressDialog progressDialog = null;
		private JSONArray array = null;
		
		@Override
		protected void onPreExecute() {
			datas.add(new BasicNameValuePair("users_id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			// display waiting dialog
			progressDialog = new ProgressDialog(FriendListFeedActivity.this);
			progressDialog.setMessage("Loading friends' feeds...please wait!");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		@Override
		  protected void onProgressUpdate(FriendFeedItem... feeds) {
			progressDialog.dismiss();
			friendFeedList.add(feeds[0]);
			adapter.notifyDataSetChanged();
	    }
		
		@Override
		protected Boolean doInBackground(Void...voids) {
			array = JsonHelper.getJsonArrayFromUrlWithData(GET_FRIEND_FEED_URL, datas);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) { 
						String snapPictureUrl = null;
						String userPictureUrl = null;
						
						if (Challenge.returnType(array.getJSONObject(i).getString("challenges_tbl_type")) == Challenge.Type.SNAP_PICTURE) {
							snapPictureUrl = array.getJSONObject(i).getString("activity_tbl_snap_picture_url");
						}
						
						if(array.getJSONObject(i).getString("users_tbl_user_image_url").equals("") == false) {
							userPictureUrl = array.getJSONObject(i).getString("users_tbl_user_image_url");
						}
						
						publishProgress(
							new FriendFeedItem.Builder(
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
										.activitySnapPictureUrl(snapPictureUrl)
										.friendPictureUrl(userPictureUrl)
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
			progressDialog.dismiss();
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
			
			progressDialog = null;
			datas = null;
			array = null;
			System.gc();
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
				finish();
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
				finish();
				break;
		}
		return true;
	}
    
    @Override
    public void onPause() {
    	Log.v(TAG, "I'm paused!");
        super.onPause();
	}
    
    @Override
    public void onDestroy() {
    	Log.v(TAG, "I'm destroyed!");
        listview = null;
        adapter = null;
        friendFeedList = null;
        System.gc();
        super.onDestroy();
	}
}