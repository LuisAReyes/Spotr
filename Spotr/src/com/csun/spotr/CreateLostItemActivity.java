package com.csun.spotr;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class CreateLostItemActivity extends Activity {
	private static final String TAG = "(CreateLostItemActivity)";
	private static final String GET_USER_POINTS_URL = "http://107.22.209.62/android/get_user_points.php";
	private static final String UPLOAD_SUBMIT_ITEM_URL = "http://107.22.209.62/android/submit_lost_item.php";
	
	private static Integer userPoints = 0;
	private int itemPoints = 0;
	
	private EditText editTextName;
	private EditText editTextDescription;
	private EditText editTextPoints;
	private Button buttonPointsPlus;
	private Button buttonPointsMinus;
	private Button buttonSelectImage;
	private Button buttonSubmit;
	private ImageView imageViewSelected;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_lost_item);
		
		// Hide the keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		editTextName = (EditText) findViewById(R.id.create_lost_item_xml_edittext_name);
		editTextDescription = (EditText) findViewById(R.id.create_lost_item_xml_edittext_description);
		editTextPoints = (EditText) findViewById(R.id.create_lost_item_xml_edittext_points);
		buttonSelectImage = (Button) findViewById(R.id.create_lost_item_xml_button_choose_image);
		buttonPointsPlus = (Button) findViewById(R.id.create_lost_item_xml_button_plus);
		buttonPointsMinus = (Button) findViewById(R.id.create_lost_item_xml_button_minus);
		buttonSubmit = (Button) findViewById(R.id.create_lost_item_xml_button_upload);
		imageViewSelected = (ImageView) findViewById(R.id.create_lost_item_xml_imageview_item_images);
		
		editTextName.setHint("Enter lost item's name here.");
		editTextDescription.setHint("Describe the item in as much detail as you can here.");
		editTextPoints.setText(Integer.toString(itemPoints));
		
		buttonSelectImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				i.setType("image/*");
				startActivityForResult(i, 0);
			}		
		});
		
		buttonPointsPlus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				itemPoints++;
				editTextPoints.setText(Integer.toString(itemPoints));
			}
		});
		
		buttonPointsMinus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				itemPoints--;
				editTextPoints.setText(Integer.toString(itemPoints));
			}
		});
		
		buttonSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Submit lost item
			}
		});
		
		Log.d(TAG, "currentUser: " + CurrentUser.getCurrentUser().getId());
		
		// Get user's current points (via AsyncTask)
		new GetUserPoints(this, CurrentUser.getCurrentUser().getId()).execute();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if(resultCode == RESULT_OK) {
			Uri selectedImageUri = intent.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			
			Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
			cursor.moveToFirst();

			ContentResolver cr = getContentResolver();
			InputStream in = null;
			try {
				in = cr.openInputStream(selectedImageUri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			Options options = new Options();
			options.inSampleSize = 8;
			Bitmap preview = BitmapFactory.decodeStream(in, null, options);
			imageViewSelected.setImageBitmap(preview);
		}
	}
	
	// We need the user's points to determine how many he/she can distribute
	private static class GetUserPoints extends AsyncTask<Void, Integer, Boolean> {
		private WeakReference<CreateLostItemActivity> refActivity;
		private List<NameValuePair> jsonList = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private int userId;
		
		public GetUserPoints(Activity c, int id) {
			refActivity = new WeakReference<CreateLostItemActivity>((CreateLostItemActivity)c);
			userId = id;
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(refActivity.get());
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			jsonList.add(new BasicNameValuePair("id", Integer.toString(userId)));
			JSONArray jsonArray = JsonHelper.getJsonArrayFromUrlWithData(GET_USER_POINTS_URL, jsonList);
			if(jsonArray != null) {
				try {
					if(jsonArray.getJSONObject(0).getString("users_tbl_points") != null) {
						userPoints = jsonArray.getJSONObject(0).getInt("users_tbl_points");
						Log.d(TAG, "userPoints: " + userPoints);
					}
				} catch (JSONException e) {
					Log.e(TAG + ": GetUserPoints()", e.toString());
				}
				
				return true;
			}
			
			return false;
		}
		
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(refActivity.get()).create();
				dialogMessage.setTitle("Oops!");
				dialogMessage.setMessage("Sorry " + CurrentUser.getCurrentUser().toString() + ", it seems there was an error.");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
		}
	

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
