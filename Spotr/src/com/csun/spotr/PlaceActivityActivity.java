package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.adapter.PlaceFeedItemAdapter;
import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.adapter_item.PlaceFeedItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PlaceActivityActivity extends Activity {
	private static final String TAG = "(PlaceActivityActivity)";
	private static final String GET_PLACE_FEED_URL = "http://107.22.209.62/android/get_activities.php";
	private int currentPlaceId = 0;
	private ListView listview = null;
	private PlaceFeedItemAdapter adapter = null;
	private List<PlaceFeedItem> placeFeedList = new ArrayList<PlaceFeedItem>();
	private boolean loading = true;
	private int prevTotal = 0;
	private final int threshHold = 5;
	private int counter = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity);
        
        Bundle extrasBundle = getIntent().getExtras();
		currentPlaceId = extrasBundle.getInt("place_id");
		
		listview = (ListView) findViewById(R.id.place_activity_xml_listview);
		adapter = new PlaceFeedItemAdapter(PlaceActivityActivity.this, placeFeedList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			}
		});
		
		
		// initial task
		new GetPlaceFeedTask(true).execute(counter);
		
		// as we scroll down the list, add more items
		listview.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (loading) {
					if (totalItemCount > prevTotal) {
						loading = false;
						prevTotal = totalItemCount;
						Log.d(TAG, Integer.toString(totalItemCount));
					}
				}

				if (!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + threshHold))) {
					synchronized (this) {
						counter += threshHold;
						loading = true;
					}
					// run with another 5
					new GetPlaceFeedTask(false).execute(counter);
				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
		});
    }
    
    private class GetPlaceFeedTask extends AsyncTask<Integer, PlaceFeedItem, Boolean> {
		private List<NameValuePair> placeData = new ArrayList<NameValuePair>(); 
		private ProgressDialog progressDialog = null;
		private boolean displayDialogFlag = false;
		
		public GetPlaceFeedTask(boolean flag) {
			displayDialogFlag = flag;
		}
		
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(PlaceActivityActivity.this);
			progressDialog.setMessage("Loading place activities...please wait!");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		@Override
	    protected void onProgressUpdate(PlaceFeedItem... feeds) {
			progressDialog.dismiss();
			placeFeedList.add(feeds[0]);
			adapter.notifyDataSetChanged();
	    }
		
		@Override
		protected Boolean doInBackground(Integer... offsets) {
			placeData.add(new BasicNameValuePair("spots_id", Integer.toString(currentPlaceId)));
			placeData.add(new BasicNameValuePair("offset", Integer.toString(offsets[0])));
			JSONArray array = JsonHelper.getJsonArrayFromUrlWithData(GET_PLACE_FEED_URL, placeData);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) { 
						String userPictureUrl = null;
						String snapPictureUrl = null;
					
						if(array.getJSONObject(i).getString("users_tbl_user_image_url") != null) {
							userPictureUrl = array.getJSONObject(i).getString("users_tbl_user_image_url");
						}
						
						if (Challenge.returnType(array.getJSONObject(i).getString("challenges_tbl_type")) == Challenge.Type.SNAP_PICTURE) {
							snapPictureUrl = array.getJSONObject(i).getString("activity_tbl_snap_picture_url");
						}
						
						publishProgress(
							new PlaceFeedItem.Builder(array.getJSONObject(i).getInt("activity_tbl_id"),
								array.getJSONObject(i).getString("users_tbl_username"),
								Challenge.returnType(array.getJSONObject(i).getString("challenges_tbl_type")),
								array.getJSONObject(i).getString("activity_tbl_created"))
									.name(array.getJSONObject(i).getString("challenges_tbl_name"))
									.comment(array.getJSONObject(i).getString("activity_tbl_comment"))
									.description(array.getJSONObject(i).getString("challenges_tbl_description"))
									.userPictureUrl(userPictureUrl)
									.snapPictureUrl(snapPictureUrl)
										.build());
						
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetPlaceFeedTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == false && displayDialogFlag == true) {
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
    public void onDestroy() {
    	Log.v(TAG, "I'm destroyed!");
        super.onDestroy();
	}
    
    @Override
    public void onPause() {
    	Log.v(TAG, "I'm paused!");
    	super.onPause();
    }
}
