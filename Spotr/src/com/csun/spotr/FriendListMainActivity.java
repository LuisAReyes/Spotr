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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;
import android.view.View;

import com.csun.spotr.core.User;
import com.csun.spotr.core.CurrentUser;
import com.csun.spotr.gui.FriendListMainItemAdapter;
import com.csun.spotr.helper.JsonHelper;

public class FriendListMainActivity extends Activity {
	private static final String TAG = "FriendListMainActivity";
	private static final String GET_FRIENDS_URL = "http://107.22.209.62/android/get_friends.php";
	private ListView listViewUser;
	private FriendListMainItemAdapter userItemAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
		// testing purpose
		// CurrentUser.setCurrentUser(6, "vlad", "somepass");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_main);
		GetFriendTask task = new GetFriendTask();
		task.execute();
	}
	
	private class GetFriendTask extends AsyncTask<Void, Integer, List<User>> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>(); 
		private  ProgressDialog progressDialog = null;
		
		@Override
		protected void onPreExecute() {
			userData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			// display waiting dialog
			progressDialog = new ProgressDialog(FriendListMainActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected List<User> doInBackground(Void...voids) {
			List<User> userList = new ArrayList<User>();
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_FRIENDS_URL, userData);
			try {
				for (int i = 0; i < array.length(); ++i) { 
					userList.add(
						new User.Builder(
							array.getJSONObject(i).getInt("id"),
							array.getJSONObject(i).getString("username"),
							array.getJSONObject(i).getString("password")).build());
					
					Log.d(TAG, userList.get(i).getUsername());
					Log.d(TAG, userList.get(i).getPassword());
				}
			}
			catch (JSONException e) {
				Log.e(TAG + "GetFriendTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return userList;
		}
		
		@Override
		protected void onPostExecute(final List<User> userList) {
			progressDialog.dismiss();
			listViewUser = (ListView) findViewById(R.id.friend_list_main_xml_listview_friends);
			userItemAdapter = new FriendListMainItemAdapter(FriendListMainActivity.this, userList, R.drawable.adium);
			listViewUser.setAdapter(userItemAdapter);
			listViewUser.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					startDialog(userList.get(position));
				}
			});
		}
	}
	
	private void startDialog(final User user) {
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
				Intent intent = new Intent("com.csun.spotr.ProfileMainActivity");
				Bundle extras = new Bundle();
				extras.putInt("user_id", user.getId());
				intent.putExtras(extras);
				startActivity(intent);
			}
		});
		myAlertDialog.show();
	}
}
