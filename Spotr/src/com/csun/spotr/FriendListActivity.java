package com.csun.spotr;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;
import android.view.View;

import com.csun.spotr.core.adapter_item.UserItem;
import com.csun.spotr.adapter.UserItemAdapter;
import com.csun.spotr.core.User;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.ImageHelper;
import com.csun.spotr.util.JsonHelper;

public class FriendListActivity extends Activity {
	private static final String TAG = "(FriendListActivity)";
	private static final String GET_FRIENDS_URL = "http://107.22.209.62/android/get_friends.php";
	private ListView listview = null;
	private UserItemAdapter adapter = null;
	private List<UserItem> userItemList = new ArrayList<UserItem>();
	private GetFriendsTask task = null;
	private boolean loading = true;
	private int prevTotal = 0;
	private final int threshHold = 10;
	private int counter = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_main);

		// initialize list view
		listview = (ListView) findViewById(R.id.friend_list_main_xml_listview_friends);

		// set up list view adapter
		adapter = new UserItemAdapter(FriendListActivity.this, userItemList);
		listview.setAdapter(adapter);
		listview.setVisibility(View.VISIBLE);

		// handle item click event
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startDialog(userItemList.get(position));
			}
		});

		// initially, we load 10 items and show user immediately
		task = new GetFriendsTask(true);
		task.execute(counter);

		// handle scrolling event
		listview.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (loading) {
					if (totalItemCount > prevTotal) {
						loading = false;
						prevTotal = totalItemCount;
						Log.d(TAG, Integer.toString(totalItemCount));
					}
				}

				if (!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + threshHold))) {
					counter += 10;
					loading = true;
					new GetFriendsTask(false).execute(counter);
				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
		});
	}

	private class GetFriendsTask extends AsyncTask<Integer, UserItem, Boolean> {
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONArray userJsonArray = null;
		private boolean displayDialogFlag;

		public GetFriendsTask(boolean flag) {
			this.displayDialogFlag = flag;
		}
		
		@Override
		protected void onPreExecute() {
			if (displayDialogFlag == true) {
				// display waiting dialog
				progressDialog = new ProgressDialog(FriendListActivity.this);
				progressDialog.setMessage("Loading friends...");
				progressDialog.setIndeterminate(true);
				progressDialog.setCancelable(false);
				progressDialog.show();
			}
		}

		@Override
		protected void onProgressUpdate(UserItem... users) {
			userItemList.add(users[0]);
			adapter.notifyDataSetChanged();
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
			if (displayDialogFlag)
				progressDialog.dismiss();
			if (result == false && displayDialogFlag == true) {
				AlertDialog dialogMessage = new AlertDialog.Builder(FriendListActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("You current have no friends");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
			
			clientData = null;
			userJsonArray = null;
			progressDialog = null;
			System.gc();
		}
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

		listview = null;
		adapter = null;
		userItemList = null;
		task = null;

		System.gc();
		super.onDestroy();
	}
}
