package com.csun.spotr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.skeleton.IActivityProgressUpdate;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.adapter.FriendFeedItemAdapter;
import com.csun.spotr.adapter.PlaceFeedItemAdapter;
import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.adapter_item.FriendFeedItem;
import com.csun.spotr.core.adapter_item.PlaceFeedItem;

import android.app.Activity;
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

/**
 * Description:
 * 		Display feeds of a place
 */
public class PlaceActivityActivity 
	extends Activity 
		implements IActivityProgressUpdate<FriendFeedItem> {
	
	private static final 	String 					TAG = "(PlaceActivityActivity)";
	private static final 	String 					GET_PLACE_FEED_URL = "http://107.22.209.62/android/get_activities.php";
	
	public 					int 					currentPlaceId = 0;
	private 				ListView 				listview = null;
	private 				FriendFeedItemAdapter   adapter = null;
	private 				List<FriendFeedItem> 	placeFeedList = new ArrayList<FriendFeedItem>();
	private 				boolean 				loading = true;
	private 				int 					prevTotal = 0;
	private final 			int 					threshHold = 5;
	private 				int 					counter = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list_feed);
        
        Bundle extrasBundle = getIntent().getExtras();
		currentPlaceId = extrasBundle.getInt("place_id");
		
		listview = (ListView) findViewById(R.id.friend_list_feed_xml_listview);
		adapter = new FriendFeedItemAdapter(getApplicationContext(), placeFeedList);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
		
		
		// initial task
		new GetPlaceFeedTask(this).execute(counter);
		
		// as we scroll down the list, add more items
		listview.setOnScrollListener(new OnScrollListener() {
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (loading) {
					if (totalItemCount > prevTotal) {
						loading = false;
						prevTotal = totalItemCount;
					}
				}

				if (!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + threshHold))) {
					synchronized (this) {
						counter += threshHold;
						loading = true;
					}
					// run with another 5
					new GetPlaceFeedTask(PlaceActivityActivity.this).execute(counter);
				}
			}

			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
		});
    }
    
    private static class GetPlaceFeedTask 
    	extends AsyncTask<Integer, FriendFeedItem, Boolean> 
    		implements IAsyncTask<PlaceActivityActivity> {
    	
		private List<NameValuePair> placeData = new ArrayList<NameValuePair>(); 
		private WeakReference<PlaceActivityActivity> ref;
		
		public GetPlaceFeedTask(PlaceActivityActivity a) {
			attach(a);
		}
		
		@Override
		protected void onPreExecute() {
		}
		
		@Override
	    protected void onProgressUpdate(FriendFeedItem... f) {
			ref.get().updateAsyncTaskProgress(f[0]);
	    }
		
		@Override
		protected Boolean doInBackground(Integer... offsets) {
			placeData.add(new BasicNameValuePair("spots_id", Integer.toString(ref.get().currentPlaceId)));
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
								new FriendFeedItem.Builder(
										// required parameters
										array.getJSONObject(i).getInt("activity_tbl_id"),
										0,
										array.getJSONObject(i).getString("users_tbl_username"),
										Challenge.returnType(array.getJSONObject(i).getString("challenges_tbl_type")),
										array.getJSONObject(i).getString("activity_tbl_created"),
										array.getJSONObject(i).getString("spots_tbl_name"))
											// optional parameters
											.challengeName(array.getJSONObject(i).getString("challenges_tbl_name"))
											.challengeDescription(array.getJSONObject(i).getString("challenges_tbl_description"))
											.activitySnapPictureUrl(snapPictureUrl)
											.friendPictureUrl(userPictureUrl)
											.activityComment(array.getJSONObject(i).getString("activity_tbl_comment"))
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
			detach();
		}

		public void attach(PlaceActivityActivity a) {
			ref = new WeakReference<PlaceActivityActivity>(a);
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
    public void onDestroy() {
    	Log.v(TAG, "I'm destroyed!");
        super.onDestroy();
	}
    
    @Override
    public void onPause() {
    	Log.v(TAG, "I'm paused!");
    	super.onPause();
    }

	public void updateAsyncTaskProgress(FriendFeedItem f) {
		placeFeedList.add(f);
		adapter.notifyDataSetChanged();
	}
}
