package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.core.User;
import com.csun.spotr.util.ImageLoader;
import com.csun.spotr.util.JsonHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendProfileActivity extends Activity {
	private static final String TAG = "(FriendProfileActivity)";
	private static final String GET_USER_DETAIL_URL = "http://107.22.209.62/android/get_user_detail.php";
	private ImageView imageViewFriendPicture;
	private TextView textViewName;
	private TextView textViewChallengesDone;
	private TextView textViewPlacesVisited;
	private TextView textViewPoint;
	private ImageLoader imageLoader;
	private User user;
	private int friendId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_profile);
		friendId = getIntent().getExtras().getInt("user_id");
		imageLoader = new ImageLoader(getApplicationContext());
		imageViewFriendPicture = (ImageView) findViewById(R.id.friend_profile_xml_friend_picture);
		textViewName = (TextView) findViewById(R.id.friend_profile_xml_name);
		textViewChallengesDone = (TextView) findViewById(R.id.friend_profile_xml_challenges_done);
		textViewPlacesVisited = (TextView) findViewById(R.id.friend_profile_xml_places_visited);
		textViewPoint = (TextView) findViewById(R.id.friend_profile_xml_points);
		
		new GetUserDetailTask().execute();
	}
	
	private class GetUserDetailTask extends AsyncTask<Void, Integer, User> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONArray array = null;
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(FriendProfileActivity.this);
			progressDialog.setMessage("Loading ...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
			
		}

		@Override
		protected User doInBackground(Void...voids) {
			userData.add(new BasicNameValuePair("user_id", Integer.toString(friendId)));
			array = JsonHelper.getJsonArrayFromUrlWithData(GET_USER_DETAIL_URL, userData);
			try {
				user = new User.Builder( 
						// required parameters
						array.getJSONObject(0).getInt("users_tbl_id"), 
						array.getJSONObject(0).getString("users_tbl_username"), 
						array.getJSONObject(0).getString("users_tbl_password"))
							// optional parameters
							.challengesDone(array.getJSONObject(0).getInt("users_tbl_challenges_done"))
							.placesVisited(array.getJSONObject(0).getInt("users_tbl_places_visited"))
							.points(array.getJSONObject(0).getInt("users_tbl_points"))
							.imageUrl(array.getJSONObject(0).getString("users_tbl_user_image_url"))
								.build();
			}
			catch (JSONException e) {
				Log.e(TAG + "GetUserDetailTask.doInBackground() : ", "JSON error parsing data" + e.toString());
			}
			return user;
		}
		
		@Override
		protected void onPostExecute(final User user) {
			progressDialog.dismiss();
			if (user != null) {
				imageLoader.displayImage(user.getImageUrl(), imageViewFriendPicture);
				textViewName.setText(user.getUsername());
				textViewChallengesDone.setText(Integer.toString(user.getChallengesDone()));
				textViewPlacesVisited.setText(Integer.toString(user.getPlacesVisited()));
				textViewPoint.setText(Integer.toString(user.getPoints()));
			}
			
			progressDialog = null;
			userData = null;
			array = null;
			
			System.gc();
		}
	}
}
