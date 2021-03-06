package com.csun.spotr;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.singleton.CurrentDateTime;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.Base64;
import com.csun.spotr.util.UploadFileHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Description:
 * 		Take a picture and upload to server
 */
public class SnapPictureActivity 
	extends Activity {

	private static final 	String 			TAG = "(SnapPictureActivity)";
	private static final 	String 			SNAP_PICTURE_URL = "http://107.22.209.62/images/upload_picture.php";
	
	private 				Button 			buttonGo = null;
	private 				Button 			buttonNext = null;
	private 				ImageView 		imageViewPreview = null;
	private 				Bitmap 			takenPictureBitmap = null;
	
	private 				String 			usersId;
	private 				String 			spotsId;
	private 				String 			challengesId;
	private 				String 			comment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snap_picture);
		
		Bundle extras = getIntent().getExtras();
		usersId = extras.getString("users_id");
		spotsId = extras.getString("spots_id");
		challengesId = extras.getString("challenges_id");
		// dummy comment
		comment = "hello snap picture";
		// initialize two buttons	
		buttonGo = (Button) findViewById(R.id.snap_picture_xml_button_go);
		buttonNext = (Button) findViewById(R.id.snap_picture_xml_button_next);
		// initialize image view
		imageViewPreview = (ImageView) findViewById(R.id.snap_picture_xml_imageview_preview_picture);
		buttonGo.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			// here is the image from camera
			takenPictureBitmap = (Bitmap) data.getExtras().get("data");
			imageViewPreview.setImageBitmap(takenPictureBitmap);
			// only enable this button when data is available
			buttonNext.setEnabled(true);
			buttonNext.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// disable both buttons to avoid user hit the back button, then hit upload again
					buttonNext.setEnabled(false);
					buttonGo.setEnabled(false);
					// start upload picture to server
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					// compress picture and add to stream (PNG)
					takenPictureBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
					// create raw data src
					byte[] src = stream.toByteArray();
					// encode it
					String byteCode = Base64.encodeBytes(src);
					UploadPictueTask task = new UploadPictueTask(SnapPictureActivity.this, byteCode);
					task.execute();
				}
			});
		}
	}

	private static class UploadPictueTask 
		extends AsyncTask<Void, Integer, String> 
			implements IAsyncTask<SnapPictureActivity> {
		
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		private WeakReference<SnapPictureActivity> ref;
		private String picturebyteCode;
		
		public UploadPictueTask(SnapPictureActivity a, String pbc) {
			attach(a);
			picturebyteCode = pbc;
		}
		
		@Override
		protected void onPreExecute() {
			
		}

		@Override
		protected String doInBackground(Void... voids) {
			// send encoded data to server
			clientData.add(new BasicNameValuePair("image", picturebyteCode));
			
			// send a file name where file name = "username" + "current date time UTC", to make sure that we have a unique id picture every time.
			// since the username is unique, we should take advantage of this otherwise two or more users could potentially snap pictures at the same time.
			clientData.add(new BasicNameValuePair("file_name",  CurrentUser.getCurrentUser().getUsername() + CurrentDateTime.getUTCDateTime().trim() + ".png"));
			
			// send the rest of data
			clientData.add(new BasicNameValuePair("users_id", ref.get().usersId));
			clientData.add(new BasicNameValuePair("spots_id", ref.get().spotsId));
			clientData.add(new BasicNameValuePair("challenges_id", ref.get().challengesId));
			
			// TODO: comment should be added
			clientData.add(new BasicNameValuePair("comment", ref.get().comment));
			
			// get JSON to check result
			JSONObject json = UploadFileHelper.uploadFileToServer(SNAP_PICTURE_URL, clientData);
		
			String result = "";
			try {
				result = json.getString("result");
			} 
			catch (JSONException e) {
				Log.e(TAG + "UploadPictueTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result.equals("success")) {
				Toast.makeText(ref.get().getApplicationContext(), "Upload picture done!", Toast.LENGTH_SHORT).show();
			}
			
			detach();
		}
		
		public void attach(SnapPictureActivity a) {
			ref = new WeakReference<SnapPictureActivity>(a);
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
	public void onDestroy() {
		Log.v(TAG, "I'm destroyed!");
		if (takenPictureBitmap != null) {
			takenPictureBitmap.recycle(); 
			takenPictureBitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public void onRestart() {
		Log.v(TAG, "I'm restarted!");
		super.onRestart();
	}

	@Override
	public void onStop() {
		Log.v(TAG, "I'm stopped!");
		super.onStop();
	}

	@Override
	public void onPause() {
		Log.v(TAG, "I'm paused!");
		super.onPause();
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
}
