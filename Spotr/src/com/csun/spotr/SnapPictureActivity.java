package com.csun.spotr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.helper.Base64;
import com.csun.spotr.helper.JsonHelper;
import com.csun.spotr.helper.UploadFileHelper;
import com.csun.spotr.singleton.PictureCountGenerator;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SnapPictureActivity extends Activity {
	private static final String TAG = "[SnapPictureActivity]";
	private static final String SNAP_PICTURE_URL = "http://107.22.209.62/images/upload_file_and_insert_to_database.php";
	private Button buttonGo;
	private Button buttonNext;
	private ImageView imageViewPreview;
	private Bitmap takenPictureBitmap;
	private String usersId;
	private String spotsId;
	private String challengesId;
	private String comment;

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
					UploadPictueTask task = new UploadPictueTask();
					task.execute();
				}
			});
		}
	}

	private class UploadPictueTask extends AsyncTask<Void, Integer, String> {
		ProgressDialog progressDialog = new ProgressDialog(SnapPictureActivity.this);
		private List<NameValuePair> snapPictureData = new ArrayList<NameValuePair>();
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(SnapPictureActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			// create a stream
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// compress picture and add to stream (PNG)
			takenPictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			// create raw data src
			byte[] src = stream.toByteArray();
			// encode it
			String byteCode = Base64.encodeBytes(src);
			// send encoded data to server
			snapPictureData.add(new BasicNameValuePair("image", byteCode));
			// send a file name
			snapPictureData.add(new BasicNameValuePair("file_name", PictureCountGenerator.getValue()));
			// send the rest of data
			snapPictureData.add(new BasicNameValuePair("users_id", usersId));
			snapPictureData.add(new BasicNameValuePair("spots_id", spotsId));
			snapPictureData.add(new BasicNameValuePair("challenges_id", challengesId));
			// TODO: comment should be added
			snapPictureData.add(new BasicNameValuePair("comment", comment));
			// get JSON to check result
			JSONObject json = UploadFileHelper.uploadFileToServer(SNAP_PICTURE_URL, snapPictureData);
			String result = "";
			try {
				result = json.getString("result");
			} 
			catch (JSONException e) {
				Log.e(TAG + "SnapPictureTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			progressDialog.dismiss();
			if (result.equals("success")) {
				Intent intent = new Intent("com.csun.spotr.PlaceActivityActivity");
				intent.putExtra("place_id", Integer.parseInt(spotsId));
				startActivity(intent);
			}
		}
	}

}
