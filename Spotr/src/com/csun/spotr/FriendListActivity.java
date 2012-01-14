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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.util.Log;
import android.view.View;

import com.csun.spotr.core.User;
import com.csun.spotr.singleton.CurrentUriList;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.gui.FriendListMainItemAdapter;
import com.csun.spotr.helper.ImageHelper;
import com.csun.spotr.helper.JsonHelper;

public class FriendListActivity extends Activity {
	private static final String TAG = "(FriendListActivity)";
	private static final String GET_FRIENDS_URL = "http://107.22.209.62/android/get_friends.php";
	private ListView list = null;
	private FriendListMainItemAdapter adapter = null;
	private List<User> userList = new ArrayList<User>();
	private GetFriendsTask task = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list_main);
		
		// initialize list view
		list = (ListView) findViewById(R.id.friend_list_main_xml_listview_friends);
		adapter = new FriendListMainItemAdapter(FriendListActivity.this, userList);
		list.setAdapter(adapter);
		list.setVisibility(View.VISIBLE);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startDialog(userList.get(position));
			}
		});
		// run task
		task = new GetFriendsTask();
		task.execute();
	}
	
	private class GetFriendsTask extends AsyncTask<Void, User, Boolean> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>(); 
		private ProgressDialog progressDialog = null;
		private JSONArray array = null;
		
		@Override
		protected void onPreExecute() {
			userData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			// display waiting dialog
			progressDialog = new ProgressDialog(FriendListActivity.this);
			progressDialog.setMessage("Loading friends...please wait");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		@Override
	    protected void onProgressUpdate(User... users) {
			userList.add(users[0]);
			adapter.notifyDataSetChanged();
	    }
		
		@Override
		protected Boolean doInBackground(Void...voids) {
			// initialize list of user
			array = JsonHelper.getJsonArrayFromUrlWithData(GET_FRIENDS_URL, userData);
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
					Log.e(TAG + "GetFriendTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
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
			AlertDialog dialogMessage = null;
			if (result == false) {
				dialogMessage = new AlertDialog.Builder(FriendListActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("You don't have any friend yet!");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
			
			progressDialog = null;
			dialogMessage = null;
			userData = null;
			array = null;
			
			System.gc();
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
				finish();
			}
		});
		myAlertDialog.show();
	}
	
	private Uri constructUriFromBitmap(Bitmap bitmap) {
		if (bitmap == null)
			return null;
		
		ContentValues values = new ContentValues(1);
		values.put(Media.MIME_TYPE, "image/jpeg");
		Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
		OutputStream outStream;
		try {
		    outStream = getContentResolver().openOutputStream(uri);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outStream);
		    outStream.close();
		} 
		catch (Exception e) {
		    Log.e(TAG, "exception while writing image", e);
		}
		
		bitmap.recycle();
		bitmap = null;
		outStream = null;
		
		System.gc();
		return uri;
	}
	
	@Override
    public void onPause() {
		Log.v(TAG, "I'm paused!");
		super.onPause();
	}
	
	@Override
    public void onDestroy() {
		Log.v(TAG, "I'm destroyed!");
		// clean up
        for (User user : userList) {
        	getContentResolver().delete(user.getImageUri(), null, null);
        	user.setImageUri(null);
        }
        
        list = null;
		adapter = null;
		userList = null;
		task = null;
		
		System.gc();
        super.onDestroy();
	}
}
