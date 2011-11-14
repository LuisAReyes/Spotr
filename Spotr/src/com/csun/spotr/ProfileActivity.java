package com.csun.spotr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * @author: Chan Nguyen
 */
public class ProfileActivity extends Activity {
	private final int CAMERA_PICTURE = 111;
	private final int GALLERY_PICTURE = 222;
	private ImageView userPictureImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		userPictureImageView = (ImageView) findViewById(R.id.image_view);
		userPictureImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startDialog();
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GALLERY_PICTURE) {
			Uri selectedImageUri = data.getData();
			String selectedImagePath = getPath(selectedImageUri);
			Bitmap myBitmap = BitmapFactory.decodeFile(selectedImagePath);
			ImageView myImage = (ImageView) findViewById(R.id.image_view);
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