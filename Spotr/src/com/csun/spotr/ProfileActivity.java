package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.core.User;
import com.csun.spotr.gui.ProfileItemAdapter;
import com.csun.spotr.helper.DownloadImageHelper;
import com.csun.spotr.helper.JsonHelper;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends Activity {
	private static final String TAG = "[ProfileActivity]";
	private static final String GET_USER_DETAIL_URL = "http://107.22.209.62/android/get_user_detail.php";
	private static final int CAMERA_PICTURE = 111;
	private static final int GALLERY_PICTURE = 222;
	private ImageView userPictureImageView = null;
	private ListView profileList = null;
	private ProfileItemAdapter		adapter = null;
	private int currentUserId = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		// testing
		Bundle extrasBundle = getIntent().getExtras();
		currentUserId = extrasBundle.getInt("user_id");
		
		userPictureImageView = (ImageView) findViewById(R.id.profile_xml_imageview_user_picture);
		userPictureImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startDialog();
			}
		});
		
		GetUserDetailTask task = new GetUserDetailTask();
		task.execute();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == GALLERY_PICTURE) {
				Uri selectedImageUri = data.getData();
				String selectedImagePath = getPath(selectedImageUri);
				Bitmap myBitmap = BitmapFactory.decodeFile(selectedImagePath);
				ImageView myImage = (ImageView) findViewById(R.id.profile_xml_imageview_user_picture);
				myImage.setImageBitmap(myBitmap);
			}
			else if (requestCode == CAMERA_PICTURE) {
				if (data.getExtras() != null) {
					// here is the image from camera
					Bitmap bitmap = (Bitmap) data.getExtras().get("data");
					userPictureImageView.setImageBitmap(bitmap);
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

	private class GetUserDetailTask extends AsyncTask<Void, Integer, User> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;

		@Override
		protected User doInBackground(Void...voids) {
			userData.add(new BasicNameValuePair("user_id", Integer.toString(currentUserId)));
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_USER_DETAIL_URL, userData);
			User user = null;
			try {
				user = new User.Builder( 
						array.getJSONObject(0).getInt("id"), 
						array.getJSONObject(0).getString("username"), 
						array.getJSONObject(0).getString("password"))
							.challengesDone(array.getJSONObject(0).getInt("challenges_done"))
							.placesVisited(array.getJSONObject(0).getInt("places_visited"))
							.points(array.getJSONObject(0).getInt("points"))
							.imageUrl(array.getJSONObject(0).getString("user_image_url"))
								.build();
			}
			catch (JSONException e) {
				Log.e(TAG + "GetUserDetailTask.doInBackground() : ", "JSON error parsing data" + e.toString());
			}
			return user;
		}
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(ProfileActivity.this);
			progressDialog.setMessage("Loading ...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
	
		@Override
		protected void onPostExecute(final User user) {
			progressDialog.dismiss();
			
			userPictureImageView.setImageDrawable(DownloadImageHelper.getImageFromUrl(user.getImageUrl()));
					
			TextView textViewChallengesDone = (TextView) findViewById(R.id.profile_xml_textview_challenges_done);
			textViewChallengesDone.setText(Integer.toString(user.getChallengesDone()));
			
			TextView textViewPlacesVisited = (TextView) findViewById(R.id.profile_xml_textview_places_visited);
			textViewPlacesVisited.setText(Integer.toString(user.getPlacesVisited()));
			
			TextView textViewPoints = (TextView) findViewById(R.id.profile_xml_textview_points);
			textViewPoints.setText(Integer.toString(user.getPoints()));
			
			List<String> headers = new ArrayList<String>();
			List<String> bodies = new ArrayList<String>();
			
			headers.add("Name");
			bodies.add(user.getUsername());
			headers.add("Password");
			bodies.add(user.getPassword());
			
			profileList = (ListView) findViewById(R.id.profile_xml_listview_items);
			adapter = new ProfileItemAdapter(ProfileActivity.this, headers, bodies);
			profileList.setAdapter(adapter);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.profile_setting_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.profile_setting_menu_xml_edit :
			intent = new Intent("com.csun.spotr.ProfileEditActivity");
			startActivity(intent);
			break;
		case R.id.profile_setting_menu_xml_preference :
			intent = new Intent("com.csun.spotr.ProfilePreferenceActivity");
			startActivity(intent);
			break;
		}
		return true;
	}
}