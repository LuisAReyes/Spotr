package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.adapter.FriendRequestItemAdapter;
import com.csun.spotr.core.adapter_item.FriendRequestItem;
import com.csun.spotr.custom_gui.DraggableGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {
	private static final String TAG = "(MainMenuActivity)";
	private static final String GET_REQUEST_URL = "http://107.22.209.62/android/get_friend_requests.php";
	private static final String ADD_FRIEND_URL = "http://107.22.209.62/android/add_friend.php";
	private static final String IGNORE_FRIEND_URL = "http://107.22.209.62/android/ignore_friend.php";
	private List<FriendRequestItem> friendRequestList = null;
	private int currentSelectedFriendId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_menu_original);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

		DraggableGridView mainGrid = (DraggableGridView) findViewById(R.id.main_menu_xml_gridview_maingrid);
		
		final ImageView imageViewProfile = new ImageView(this);
		imageViewProfile.setImageDrawable(getResources().getDrawable(R.drawable.profile));
		mainGrid.addView(imageViewProfile);


		final ImageView imageViewLocalMap = new ImageView(this);
		imageViewLocalMap.setImageDrawable(getResources().getDrawable(R.drawable.challenges));
		mainGrid.addView(imageViewLocalMap);
		
		final ImageView imageViewFriend = new ImageView(this);
		imageViewFriend.setImageDrawable(getResources().getDrawable(R.drawable.friends));
		mainGrid.addView(imageViewFriend);
		
		final ImageView imageViewFinder = new ImageView(this);
		imageViewFinder.setImageDrawable(getResources().getDrawable(R.drawable.leaderboards));
		mainGrid.addView(imageViewFinder);
	
		final ImageView imageViewQuest = new ImageView(this);
		imageViewQuest.setImageDrawable(getResources().getDrawable(R.drawable.awards));
		mainGrid.addView(imageViewQuest);
		
		final ImageView imageViewPlace = new ImageView(this);
		imageViewPlace.setImageDrawable(getResources().getDrawable(R.drawable.spots));
		mainGrid.addView(imageViewPlace);
		
		final ImageView imageViewPingMap = new ImageView(this);
		imageViewPingMap.setImageDrawable(getResources().getDrawable(R.drawable.spots));
		mainGrid.addView(imageViewPingMap);
	
		mainGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent;
				if (v.equals(imageViewProfile)) {
					Bundle extras = new Bundle();
					extras.putInt("user_id", CurrentUser.getCurrentUser().getId());
					intent = new Intent(getApplicationContext(), ProfileMainActivity.class);
					intent.putExtras(extras);
					startActivity(intent);
				}
				else if (v.equals(imageViewLocalMap)) {
					intent = new Intent(getApplicationContext(), LocalMapViewActivity.class);
					startActivity(intent);
				}
				else if (v.equals(imageViewFinder)) {
					intent = new Intent(getApplicationContext(), FinderActivity.class);
					startActivity(intent);
				}
				else if (v.equals(imageViewPlace)) {
					intent = new Intent(getApplicationContext(), PlaceActivity.class);
					startActivity(intent);
				}
				else if (v.equals(imageViewFriend)) {
					intent = new Intent(getApplicationContext(), FriendListMainActivity.class);
					startActivity(intent);
				}
				else if (v.equals(imageViewQuest)) {
					intent = new Intent(getApplicationContext(), QuestActivity.class);
					startActivity(intent);
				}
				else if (v.equals(imageViewPingMap)) {
				 	intent = new Intent(getApplicationContext(), PingMapActivity.class);
					startActivity(intent);   
				}
				else {
					// should never go here
				}
		    }
		});	
	
		// populating Notification with friend request 
		GetFriendRequestTask task = new GetFriendRequestTask();
		task.execute();
	}

	private class GetFriendRequestTask extends AsyncTask<Void, Integer, Boolean> {
		private List<NameValuePair> friendData = new ArrayList<NameValuePair>(); 
		private ProgressDialog progressDialog = null;
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(MainMenuActivity.this);
			progressDialog.setMessage("Loading notification...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void...voids) {
			friendData.add(new BasicNameValuePair("users_id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			friendRequestList = new ArrayList<FriendRequestItem>();
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_REQUEST_URL, friendData);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) { 
						friendRequestList.add(
							new FriendRequestItem(
								array.getJSONObject(i).getInt("user_requests_tbl_friend_id"),
								array.getJSONObject(i).getString("users_tbl_username"),
								array.getJSONObject(i).getString("user_requests_tbl_friend_message"),
								array.getJSONObject(i).getString("user_requests_tbl_time")));
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFriendRequestTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
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
			if (result == true) {
				ListView list = (ListView) findViewById(R.id.main_menu_xml_slide_content);
				FriendRequestItemAdapter adapter = new FriendRequestItemAdapter(MainMenuActivity.this, friendRequestList);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						startDialog(position);
					}
				});
			}
			else {

			}
		}
		
		private void startDialog(final int position) {
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(MainMenuActivity.this);
			myAlertDialog.setTitle("Request Dialog");
			myAlertDialog.setMessage("Accept this message?");
			myAlertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					UpdateFriendTask task = new UpdateFriendTask();
					currentSelectedFriendId = friendRequestList.get(position).getFriendId();
					task.execute(ADD_FRIEND_URL);
				}
			});
			
			myAlertDialog.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					UpdateFriendTask task = new UpdateFriendTask();
					currentSelectedFriendId = friendRequestList.get(position).getFriendId();
					task.execute(IGNORE_FRIEND_URL);
				}
			});
			myAlertDialog.show();
		}
			
	}
	
	private class UpdateFriendTask extends AsyncTask<String, Integer, Boolean> {
		private List<NameValuePair> datas = new ArrayList<NameValuePair>(); 
		private ProgressDialog progressDialog = null;
		private boolean isAdd;
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(MainMenuActivity.this);
			progressDialog.setMessage("Processing accept task...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(String... urls) {
			if (urls[0].equals(ADD_FRIEND_URL))
				isAdd = true;
			else
				isAdd = false;
			
			datas.add(new BasicNameValuePair("users_id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			datas.add(new BasicNameValuePair("friend_id", Integer.toString(currentSelectedFriendId)));
			friendRequestList = new ArrayList<FriendRequestItem>();
			JSONObject json = JsonHelper.getJsonObjectFromUrlWithData(urls[0], datas);
			try {
				if (json.getString("result").equals("success")) {
					return true;
				}
			} 
			catch (JSONException e) {
				Log.e(TAG + "AcceptFriendTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == true) {
				if (isAdd) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(MainMenuActivity.this);
					dialog.setTitle("Friend process dialog");
					dialog.setMessage("Congratulation you have a new friend!");
					dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							GetFriendRequestTask task = new GetFriendRequestTask();
							task.execute();
						}
					});
					dialog.show();
				}
				else {
					GetFriendRequestTask task = new GetFriendRequestTask();
					task.execute();
				}
			}
			else {

			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.options_menu_xml_item_setting_icon :
			intent = new Intent("com.csun.spotr.SettingsActivity");
			startActivity(intent);
			break;
		case R.id.options_menu_xml_item_logout_icon :
			SharedPreferences.Editor editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
			editor.clear();
			editor.commit();
			intent = new Intent("com.csun.spotr.LoginActivity");
			startActivity(intent);
			break;
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        startActivity(new Intent("com.csun.spotr.LoginActivity"));
	        finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
}