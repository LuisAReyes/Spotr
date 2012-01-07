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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.core.User;
import com.csun.spotr.gui.FriendListMainItemAdapter;
import com.csun.spotr.helper.ImageHelper;
import com.csun.spotr.helper.JsonHelper;

public class FriendListActionActivity extends Activity {
	private static final String TAG = "(FriendListActionActivity)";
	private static final String SEARCH_FRIENDS_URL = "http://107.22.209.62/android/search_friends.php";
	private static final String SEND_REQUEST_URL = "http://107.22.209.62/android/send_friend_request.php";
	private EditText editTextSearch = null;
	private ListView list = null;
	private FriendListMainItemAdapter adapter = null;
	private List<User> userList = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_action);
		Button buttonSearch = (Button) findViewById(R.id.friend_list_action_xml_button_search);
		editTextSearch = (EditText) findViewById(R.id.friend_list_action_xml_edittext_search);
		
		// TODO: should we allow user to search on an empty string? which
		// returns the whole list of users in our database
		buttonSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// hide keyboard right away
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
				// create a new list of items
				userList = new ArrayList<User>();
				list = (ListView) findViewById(R.id.friend_list_action_xml_listview_search_friends);
				adapter = new FriendListMainItemAdapter(FriendListActionActivity.this, userList);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						startDialog(userList.get(position));
					}
				});
				// start task
				new SearchFriendsTask().execute(editTextSearch.getText().toString());
			}
		});
	}

	private class SearchFriendsTask extends AsyncTask<String, User, Boolean> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
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
	    protected void onProgressUpdate(User... users) {
			progressDialog.dismiss();
			userList.add(users[0]);
			adapter.notifyDataSetChanged();
			// adapter.notifyDataSetInvalidated();
	    }
		
		@Override
		protected Boolean doInBackground(String... text) {
			userData.add(new BasicNameValuePair("text", text[0].toString()));
			userData.add(new BasicNameValuePair("users_id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(SEARCH_FRIENDS_URL, userData);
			if (array != null) {
				try {
					for (int i = 0; i < array.length(); ++i) {
						publishProgress(new User.Builder(
								array.getJSONObject(i).getInt("users_tbl_id"),
								array.getJSONObject(i).getString("users_tbl_username"),
								array.getJSONObject(i).getString("users_tbl_password"))
									.imageUri(constructUriFromBitmap(ImageHelper.downloadImage(array.getJSONObject(i).getString("users_tbl_user_image_url"))))
										.build());
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "SearchFriendTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
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
		}
	}

	private void startDialog(final User user) {
		AlertDialog.Builder builder;
		Context c = this;
		final AlertDialog dialog;
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.friend_request_dialog, null);
		builder = new AlertDialog.Builder(c);
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
				task.execute(
					Integer.toString(CurrentUser.getCurrentUser().getId()),
					Integer.toString(user.getId()), 
					message);
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
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

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
			userData.add(new BasicNameValuePair("users_id", datas[0].toString()));
			userData.add(new BasicNameValuePair("friend_id", datas[1].toString()));
			userData.add(new BasicNameValuePair("friend_message", datas[2].toString()));
			JSONObject json = JsonHelper.getJsonObjectFromUrlWithData(SEND_REQUEST_URL, userData);
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
			progressDialog.dismiss();
			if (result == true) {
				AlertDialog dialogMessage = new AlertDialog.Builder(FriendListActionActivity.this).create();
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
				AlertDialog dialogMessage = new AlertDialog.Builder(FriendListActionActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("Fail to send request");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();	
			}
		}
	}
	
	private Uri constructUriFromBitmap(Bitmap bitmap) {
		ContentValues values = new ContentValues(1);
		values.put(Media.MIME_TYPE, "image/jpeg");
		Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
		try {
		    OutputStream outStream = getContentResolver().openOutputStream(uri);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 1, outStream);
		    outStream.close();
		} 
		catch (Exception e) {
		    Log.e(TAG, "exception while writing image", e);
		}
		bitmap.recycle();
		return uri;
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
		if (userList != null) {
	        for (User user : userList) {
	        	getContentResolver().delete(user.getImageUri(), null, null);
	        }
		}
        super.onDestroy();
	}
	
}
