package com.csun.spotr;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.csun.spotr.core.adapter_item.UserItem;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.adapter.UserItemAdapter;

public class FriendListActionActivity extends Activity {
	private static final String TAG = "(FriendListActionActivity)";
	private static final String SEARCH_FRIENDS_URL = "http://107.22.209.62/android/search_friends.php";
	private static final String SEND_REQUEST_URL = "http://107.22.209.62/android/send_friend_request.php";
	private ListView listview = null;
	private UserItemAdapter adapter = null;
	private List<UserItem> userItemList = null;
	private Button buttonSearch = null;
	private EditText editTextSearch = null;
	private SearchFriendsTask task = null;

	private boolean loading = true;
	private int prevTotal = 0;
	private final int threshHold = 10;
	private int counter = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_action);
		buttonSearch = (Button) findViewById(R.id.friend_list_action_xml_button_search);
		editTextSearch = (EditText) findViewById(R.id.friend_list_action_xml_edittext_search);

		buttonSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// hide keyboard right away
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);

				// create a new list of items
				listview = (ListView) findViewById(R.id.friend_list_action_xml_listview_search_friends);
				/*
				 * View footerView =
				 * ((LayoutInflater)FriendListActionActivity.this
				 * .getSystemService
				 * (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.
				 * layout.listview_footer, null, false);
				 * list.addFooterView(footerView);
				 */
				counter = 0;
				userItemList = new ArrayList<UserItem>();
				adapter = new UserItemAdapter(FriendListActionActivity.this, userItemList);
				listview.setAdapter(adapter);
				listview.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						startDialog(userItemList.get(position));
					}
				});

				// start task
				task = new SearchFriendsTask(editTextSearch.getText().toString(), true);
				task.execute(counter);

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
							new SearchFriendsTask(editTextSearch.getText().toString(), false).execute(counter);
						}
					}

					public void onScrollStateChanged(AbsListView view, int scrollState) {

					}
				});
			}
		});
	}

	private class SearchFriendsTask extends AsyncTask<Integer, UserItem, Boolean> {
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONArray array = null;
		private final String criteria;
		private boolean displayDialogFlag;

		public SearchFriendsTask(String criteria, boolean flag) {
			this.criteria = criteria;
			displayDialogFlag = flag;
		}

		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(FriendListActionActivity.this);
			progressDialog.setMessage("Searching...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(UserItem... users) {
			userItemList.add(users[0]);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected Boolean doInBackground(Integer... offsets) {
			clientData.add(new BasicNameValuePair("text", criteria));
			clientData.add(new BasicNameValuePair("users_id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			clientData.add(new BasicNameValuePair("offset", Integer.toString(offsets[0])));
			array = JsonHelper.getJsonArrayFromUrlWithData(SEARCH_FRIENDS_URL, clientData);
			if (array != null) {
				try {
					for (int i = 0; i < array.length(); ++i) {
						publishProgress(new UserItem(array.getJSONObject(i).getInt("users_tbl_id"), array.getJSONObject(i).getString("users_tbl_username"), array.getJSONObject(i).getString("users_tbl_user_image_url")));
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "SearchFriendTask.doInBackGround(Integer... offsets) : ", "JSON error parsing data" + e.toString());
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
			if (result == false && displayDialogFlag == true) {
				AlertDialog dialogMessage = new AlertDialog.Builder(FriendListActionActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("No name match this search criteria. Please try again!");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
			
			clientData = null;
			progressDialog = null;
			array = null;
			
			System.gc();
		}
	}

	private void startDialog(final UserItem user) {
		AlertDialog.Builder builder;
		final AlertDialog dialog;
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.friend_request_dialog, null);
		builder = new AlertDialog.Builder(this);
		builder.setTitle("Send request");
		builder.setView(layout);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				EditText editTextMessage = (EditText) layout.findViewById(R.id.friend_request_dialog_xml_edittext_message);
				String message = " ";
				if (editTextMessage.getText() != null) {
					message = editTextMessage.getText().toString();
				}
				SendFriendRequestTask task = new SendFriendRequestTask();
				task.execute(Integer.toString(CurrentUser.getCurrentUser().getId()), Integer.toString(user.getId()), message);
			}
		});

		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		dialog = builder.create();
		dialog.show();
	}

	private class SendFriendRequestTask extends AsyncTask<String, Integer, Boolean> {
		private List<NameValuePair> friendData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONObject json = null;

		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(FriendListActionActivity.this);
			progressDialog.setMessage("Sending request...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... datas) {
			friendData.add(new BasicNameValuePair("users_id", datas[0].toString()));
			friendData.add(new BasicNameValuePair("friend_id", datas[1].toString()));
			friendData.add(new BasicNameValuePair("friend_message", datas[2].toString()));
			json = JsonHelper.getJsonObjectFromUrlWithData(SEND_REQUEST_URL, friendData);
			try {
				if (json.getString("result").equals("success")) {
					return true;
				}
			}
			catch (JSONException e) {
				Log.e(TAG + "SnapPictureTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			AlertDialog dialogMessage = null;
			progressDialog.dismiss();
			if (result == true) {
				dialogMessage = new AlertDialog.Builder(FriendListActionActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("Request has been sent succesfully");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
			else {
				dialogMessage = new AlertDialog.Builder(FriendListActionActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("Fail to send request");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}

			progressDialog = null;
			dialogMessage = null;
			friendData = null;
			json = null;
			System.gc();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.options_menu_xml_item_setting_icon :
			intent = new Intent("com.csun.spotr.SettingsActivity");
			startActivity(intent);
			finish();
			break;
		case R.id.options_menu_xml_item_logout_icon :
			SharedPreferences.Editor editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
			editor.clear();
			editor.commit();
			intent = new Intent("com.csun.spotr.LoginActivity");
			startActivity(intent);
			finish();
			break;
		case R.id.options_menu_xml_item_mainmenu_icon :
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
		super.onDestroy();
	}
}
