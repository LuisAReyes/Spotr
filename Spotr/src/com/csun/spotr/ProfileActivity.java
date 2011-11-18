package com.csun.spotr;

import com.csun.spotr.gui.ProfileItemAdapter;

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
import android.widget.ListView;

/**
 * @author: Chan Nguyen
 */
public class ProfileActivity extends Activity {
	private final int CAMERA_PICTURE = 111;
	private final int GALLERY_PICTURE = 222;
	private ImageView userPictureImageView;
	
	// Zach Duvall (11/16/2011)
	ListView profileList;
	ProfileItemAdapter adapter;
	
	private static String headers[] = { 
		"E-mail", 
		"Name", 
		"Headline", 
		"Nickname", 
		"Location", 
		"Hometown", 
		"About me" 
	};
	private static String bodies[] = { 
		"zdduvall@gmail.com", 
		"Zach Duvall", 
		"I am awesome.", 
		"Zach Attack", 
		"Burbank, CA", 
		"Durham, NC", 
		"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam fermentum lacus ut nisl " +
		"tempor et malesuada arcu semper. Maecenas at arcu felis. Aliquam erat volutpat. Maecenas " +
		"erat sapien, eleifend et dignissim id, iaculis fringilla purus. Aenean varius dui id nisl " +
		"semper ac fermentum est convallis. Proin porttitor dolor sed massa lacinia accumsan. Proin " +
		"fermentum consectetur condimentum. Nunc ornare felis felis, quis pulvinar quam. Ut non quam " +
		"tortor, id ultricies lacus. Curabitur lobortis metus ac massa malesuada placerat. Vestibulum " +
		"porttitor pulvinar dapibus. Sed eget ipsum non arcu ornare volutpat. Sed iaculis ornare lectus " +
		"eget sodales." 
	}; // Note: I would normally put this in XML, but this is extremely temporary.  This should be a JSON
	   // string from the database.

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
		
		// Zach Duvall (11/16/2011)
		profileList = (ListView) findViewById(R.id.profile_xml_listview_items);
		adapter = new ProfileItemAdapter(this, headers, bodies);
		profileList.setAdapter(adapter);
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