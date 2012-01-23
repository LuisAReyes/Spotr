package com.csun.spotr;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.adapter.ProfileItemAdapter;
import com.csun.spotr.core.User;
import com.csun.spotr.util.ImageHelper;
import com.csun.spotr.util.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends Activity {
	private static final String TAG = "(ProfileActivity)";
	private static final String GET_USER_DETAIL_URL = "http://107.22.209.62/android/get_user_detail.php";
	private static final int CAMERA_PICTURE = 111;
	private static final int GALLERY_PICTURE = 222;
	private ListView listViewProfile = null;
	private ProfileItemAdapter adapter = null;
	private int currentUserId = 0;
	private User user = null;
	private GetUserDetailTask task = null;
	private Bitmap bitmapUserPicture = null;
	private ImageView imageViewUserPicture = null;
	private TextView textViewChallengesDone = null;
	private TextView textViewPlacesVisited = null;
	private TextView textViewPoints = null;
	private List<String> headers = new ArrayList<String>();
	private List<String> bodies = new ArrayList<String>();
				
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		// testing
		Bundle extrasBundle = getIntent().getExtras();
		currentUserId = extrasBundle.getInt("user_id");
		
		imageViewUserPicture = (ImageView) findViewById(R.id.profile_xml_imageview_user_picture);
		imageViewUserPicture.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startDialog();
			}
		});
		
		task = new GetUserDetailTask();
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

	private class GetUserDetailTask extends AsyncTask<Void, Integer, User> {
		private List<NameValuePair> userData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONArray array = null;
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(ProfileActivity.this);
			progressDialog.setMessage("Loading ...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected User doInBackground(Void...voids) {
			userData.add(new BasicNameValuePair("user_id", Integer.toString(currentUserId)));
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
							.imageUri(constructUriFromBitmap(ImageHelper.downloadImage(array.getJSONObject(0).getString("users_tbl_user_image_url"))))
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
				imageViewUserPicture.setImageURI(user.getImageUri());
				
				textViewChallengesDone = (TextView) findViewById(R.id.profile_xml_textview_challenges_done);
				textViewChallengesDone.setText(Integer.toString(user.getChallengesDone()));
				
				textViewPlacesVisited = (TextView) findViewById(R.id.profile_xml_textview_places_visited);
				textViewPlacesVisited.setText(Integer.toString(user.getPlacesVisited()));
				
				textViewPoints = (TextView) findViewById(R.id.profile_xml_textview_points);
				textViewPoints.setText(Integer.toString(user.getPoints()));
				
				headers = new ArrayList<String>();
				bodies = new ArrayList<String>();
				
				headers.add("Name");
				bodies.add(user.getUsername());
				headers.add("Password");
				bodies.add(user.getPassword());
				
				listViewProfile = (ListView) findViewById(R.id.profile_xml_listview_items);
				adapter = new ProfileItemAdapter(ProfileActivity.this, headers, bodies);
				listViewProfile.setAdapter(adapter);
			}
			
			progressDialog = null;
			userData = null;
			array = null;
			
			System.gc();
		}
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
			bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outStream);
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
		if (user != null) {
			getContentResolver().delete(user.getImageUri(), null, null);
			user.setImageUri(null);
		}
		
		if (bitmapUserPicture != null) {
			bitmapUserPicture.recycle();
			bitmapUserPicture = null;
		}
		
		listViewProfile = null;
		adapter = null;
		user = null;
		imageViewUserPicture = null;
		textViewChallengesDone = null;
		textViewPlacesVisited = null;
		textViewPoints = null;
		
		System.gc();
        super.onDestroy();
	}
}