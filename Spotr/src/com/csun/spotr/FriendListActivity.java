package com.csun.spotr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;
import android.view.View;

import com.csun.spotr.core.adapter_item.UserItem;
import com.csun.spotr.adapter.UserItemAdapter;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.skeleton.IActivityProgressUpdate;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.JsonHelper;

/**
 * Description:
 * 		This class will retrieve a list of friends from database.
 */
public class FriendListActivity 
	extends Activity 
		implements IActivityProgressUpdate<UserItem> {
	
	private static final String 				TAG = "(FriendListActivity)";
	private static final String 				GET_FRIENDS_URL = "http://107.22.209.62/android/get_friends.php";
	
	private 			 ListView 				listview = null;
	private 			 boolean 				loading = true;
	private 			 int 					prevTotal = 0;
	private final 		 int 					threshHold = 10;
	private int 								counter = 0;
	
	public 				 UserItemAdapter 		adapter = null;
	public 				 List<UserItem> 		userItemList = new ArrayList<UserItem>();
	public 				 GetFriendsTask 		task = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_main);

		// initialize list view
		listview = (ListView) findViewById(R.id.friend_list_main_xml_listview_friends);

		// set up list view adapter
		adapter = new UserItemAdapter(this.getApplicationContext(), userItemList);
		listview.setAdapter(adapter);
		listview.setVisibility(View.VISIBLE);

		// handle item click event
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startDialog(userItemList.get(position));
			}
		});

		// initially, we load 10 items and show user immediately
		task = new GetFriendsTask(this, true);
		task.execute(counter);

		// handle scrolling event
		listview.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (loading) {
					if (totalItemCount > prevTotal) {
						loading = false;
						prevTotal = totalItemCount;
					}
				}

				if (!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + threshHold))) {
					counter += 10;
					loading = true;
					new GetFriendsTask(FriendListActivity.this, false).execute(counter);
				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
		});
	}

	private static class GetFriendsTask extends AsyncTask<Integer, UserItem, Boolean> implements IAsyncTask<FriendListActivity>{
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		private WeakReference<FriendListActivity> ref;
		private JSONArray userJsonArray = null;

		public GetFriendsTask(FriendListActivity a, boolean flag) {
			attach(a);
		}
		
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(UserItem... u) {
			ref.get().updateAsyncTaskProgress(u[0]);
		}

		@Override
		protected Boolean doInBackground(Integer... offsets) {
			// send user id
			clientData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			// send offset
			clientData.add(new BasicNameValuePair("offset", Integer.toString(offsets[0])));
			// retrieve data from server
			userJsonArray = JsonHelper.getJsonArrayFromUrlWithData(GET_FRIENDS_URL, clientData);
			if (userJsonArray != null) {
				try {
					for (int i = 0; i < userJsonArray.length(); ++i) {
						publishProgress(
							new UserItem(
								userJsonArray.getJSONObject(i).getInt("users_tbl_id"), 
								userJsonArray.getJSONObject(i).getString("users_tbl_username"), 
								userJsonArray.getJSONObject(i).getString("users_tbl_user_image_url")));
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFriendTask.doInBackGround(Integer... offsets) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			detach();
		}
		
		public void attach(FriendListActivity a) {
			ref = new WeakReference<FriendListActivity>(a);
		}
		
		public void detach() {
			ref.clear();
		}
	}
	
	public void updateAsyncTaskProgress(UserItem u) {
		userItemList.add(u);
		adapter.notifyDataSetChanged();
	}

	private void startDialog(final UserItem user) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Friend Dialog");
		myAlertDialog.setMessage("Pick an option");
		myAlertDialog.setPositiveButton("Send a message", new DialogInterface.OnClickListener() {
			// do something when the button is clicked
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});

		myAlertDialog.setNegativeButton("View profile", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent("com.csun.spotr.FriendProfileActivity");
				Bundle extras = new Bundle();
				extras.putInt("user_id", user.getId());
				intent.putExtras(extras);
				startActivity(intent);
				finish();
			}
		});
		myAlertDialog.show();
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
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new 
				AlertDialog.Builder(this)
					.setIcon(R.drawable.error_circle)
					.setTitle("Error Message")
					.setMessage("No friends!")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							
						}
					}).create();
		
		case 1: 
			return new 
					AlertDialog.Builder(this)
						.setIcon(R.drawable.error_circle)
						.setTitle("Error Message")
						.setMessage("<undefined>")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								
							}
						}).create();
		}
		return null;
	}
}
