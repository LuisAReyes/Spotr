package com.csun.spotr;

import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.adapter.ProfileItemAdapter;
import com.csun.spotr.core.User;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.skeleton.IActivityProgressUpdate;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.ImageLoader;
import com.csun.spotr.util.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Description:
 * 		Display user's detail information
 */
public class ProfileActivity 
	extends Activity 
		implements IActivityProgressUpdate<User> {
	
	private static final 	String 					TAG = "(ProfileActivity)";
	private static final 	String 					GET_USER_DETAIL_URL = "http://107.22.209.62/android/get_user_detail.php";
	private static final 	int 					CAMERA_PICTURE = 111;
	private static final 	int 					GALLERY_PICTURE = 222;
	
	private 				ListView 				list = null;
	private 				ProfileItemAdapter		adapter = null;
	private 				GetUserDetailTask 		task = null;
	private 				Bitmap 					bitmapUserPicture = null;
	
	public 					ImageView 				imageViewUserPicture = null;
	public 					ImageLoader 			imageLoader;
	public 					TextView 				textViewChallengesDone = null;
	public 					TextView 				textViewPlacesVisited = null;
	public 					TextView 				textViewPoints = null;
	public 					List<String> 			headers = new ArrayList<String>();
	public 					List<String> 			bodies = new ArrayList<String>();
				
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		imageLoader = new ImageLoader(getApplicationContext());
		
		imageViewUserPicture = (ImageView) findViewById(R.id.profile_xml_imageview_user_picture);
		imageViewUserPicture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startDialog();
			}
		});
		
		task = new GetUserDetailTask(ProfileActivity.this);
		task.execute();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY_PICTURE) {
				Uri selectedImageUri = data.getData();
				String selectedImagePath = getPath(selectedImageUri);
				bitmapUserPicture = BitmapFactory.decodeFile(selectedImagePath);
				imageViewUserPicture.setImageBitmap(bitmapUserPicture);
			}
			else if (requestCode == CAMERA_PICTURE) {
				if (data.getExtras() != null) {
					// here is the image from camera
					bitmapUserPicture = (Bitmap) data.getExtras().get("data");
					imageViewUserPicture.setImageBitmap(bitmapUserPicture);
				}
			}
		}
	}

	private void startDialog() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Upload Pictures Option");
		myAlertDialog.setMessage("How do you want to set your picture?");
		myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PICTURE);
			}
		});

		myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, CAMERA_PICTURE);
			}
		});
		myAlertDialog.show();
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private static class GetUserDetailTask 
		extends AsyncTask<Void, Integer, User> 
			implements IAsyncTask<ProfileActivity> {
		
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private WeakReference<ProfileActivity> ref;
		private JSONArray array = null;
		
		public GetUserDetailTask(ProfileActivity a) {
			attach(a);
		}
		
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected User doInBackground(Void...voids) {
			userData.add(new BasicNameValuePair("user_id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			array = JsonHelper.getJsonArrayFromUrlWithData(GET_USER_DETAIL_URL, userData);
			User user = null;
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
		protected void onPostExecute(final User u) {
			if (u != null) {
				ref.get().updateAsyncTaskProgress(u);
			}
			detach();
		}

		public void attach(ProfileActivity a) {
			ref = new WeakReference<ProfileActivity>(a);
		}

		public void detach() {
			ref.clear();
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
	
		if (bitmapUserPicture != null) {
			bitmapUserPicture.recycle();
			bitmapUserPicture = null;
		}
		
        super.onDestroy();
	}

	public void updateAsyncTaskProgress(User u) {
		imageLoader.displayImage(u.getImageUrl(), imageViewUserPicture);
		
		textViewChallengesDone = (TextView) findViewById(R.id.profile_xml_textview_challenges_done);
		textViewChallengesDone.setText(Integer.toString(u.getChallengesDone()));
		
		textViewPlacesVisited = (TextView) findViewById(R.id.profile_xml_textview_places_visited);
		textViewPlacesVisited.setText(Integer.toString(u.getPlacesVisited()));
		
		textViewPoints = (TextView) findViewById(R.id.profile_xml_textview_points);
		textViewPoints.setText(Integer.toString(u.getPoints()));
		
		headers = new ArrayList<String>();
		bodies = new ArrayList<String>();
		
		headers.add("Name");
		bodies.add(u.getUsername());
		headers.add("Password");
		bodies.add(u.getPassword());
		
		list = (ListView) findViewById(R.id.profile_xml_listview_items);
		adapter = new ProfileItemAdapter(getApplicationContext(), headers, bodies);
		list.setAdapter(adapter);
		
	}
}