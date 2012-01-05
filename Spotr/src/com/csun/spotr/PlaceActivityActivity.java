package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.core.PlaceLog;
import com.csun.spotr.gui.PlaceActivityItemAdapter;
import com.csun.spotr.helper.DownloadImageHelper;
import com.csun.spotr.helper.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PlaceActivityActivity extends Activity {
	private static final String TAG = "[PlaceActivityActivity]";
	private static final String GET_PLACELOG_URL = "http://107.22.209.62/android/get_activities.php";
	private List<PlaceLog> placeLogList;
	private int currentPlaceId = 0;
	private ListView list = null;
	private PlaceActivityItemAdapter adapter = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity);
        
        Bundle extrasBundle = getIntent().getExtras();
		currentPlaceId = extrasBundle.getInt("place_id");
		
		GetPlaceLogTask task = new GetPlaceLogTask();
		task.execute();
    }
    
    private class GetPlaceLogTask extends AsyncTask<Void, Integer, Boolean> {
		private List<NameValuePair> placeData = new ArrayList<NameValuePair>(); 
		private  ProgressDialog progressDialog = null;
		
		@Override
		protected void onPreExecute() {
			placeData.add(new BasicNameValuePair("spots_id", Integer.toString(currentPlaceId)));
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActivityActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void...voids) {
			placeLogList = new ArrayList<PlaceLog>();
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_PLACELOG_URL, placeData);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) { 
						placeLogList.add(
							new PlaceLog.Builder(array.getJSONObject(i).getInt("activity_tbl_id"),
								array.getJSONObject(i).getString("users_tbl_username"),
								array.getJSONObject(i).getString("challenges_tbl_type"),
								array.getJSONObject(i).getString("activity_tbl_created"))
									.name(array.getJSONObject(i).getString("challenges_tbl_name"))
									.comment(array.getJSONObject(i).getString("activity_tbl_comment"))
									.description(array.getJSONObject(i).getString("challenges_tbl_description"))
									.userPictureDrawable(DownloadImageHelper.getImageFromUrl(array.getJSONObject(i).getString("users_tbl_user_image_url")))
									.snapPictureDrawable(DownloadImageHelper.getImageFromUrl(array.getJSONObject(i).getString("activity_tbl_snap_picture_url")))
										.build());
						
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetPlaceLogTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				progressDialog.dismiss();
				list = (ListView) findViewById(R.id.place_activity_xml_listview);
				adapter = new PlaceActivityItemAdapter(PlaceActivityActivity.this, placeLogList);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					}
				});
			}
			else {
				progressDialog.dismiss();
				AlertDialog dialogMessage = new AlertDialog.Builder(PlaceActivityActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("There are no activities for this place!");
				dialogMessage.setButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialogMessage.show();
			}
		}
			
	}
    
}
