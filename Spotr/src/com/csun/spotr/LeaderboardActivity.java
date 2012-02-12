package com.csun.spotr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.R.color;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Button;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.skeleton.IActivityProgressUpdate;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.JsonHelper;
import com.csun.spotr.adapter.LeaderboardItemAdapter;
import com.csun.spotr.core.User;

/**
 * Description:
 * Display all users with points and ranking 
 */
public class LeaderboardActivity 
	extends Activity 
		implements IActivityProgressUpdate<User> {
	
	private static final 	String 						TAG = "(LeaderboardActivity)";
	private static final 	String 						GET_USERS_URL = "http://107.22.209.62/android/get_users.php";
	
	private 				ListView 					listview = null;
	private 				LeaderboardItemAdapter 		adapter = null;
	private 				List<User> 					userList = new ArrayList<User>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);
		
		// initialize list view
		listview = (ListView) findViewById(R.id.leaderboard_xml_listview_users);
		adapter = new LeaderboardItemAdapter(getApplicationContext(), userList);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// handle click 
			}
		});
		
		new GetUsersTask(this).execute();
		
		Button buttonWhere = (Button) findViewById(R.id.leaderboard_xml_button_where_am_i);
		buttonWhere.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				// run new task
				listview.post(new Runnable() {
					public void run() {
						listview.setSelection(CurrentUser.getSelectedPosition());
						listview.getChildAt(CurrentUser.getSelectedPosition()).setBackgroundColor(Color.RED);
						v.setEnabled(false);
						v.setBackgroundColor(color.transparent);
				}});
			}
		});
	}
	
	private static class GetUsersTask 
		extends AsyncTask<Void, User, Boolean> 
			implements IAsyncTask<LeaderboardActivity> {
		
		private WeakReference<LeaderboardActivity> ref;
		
		public GetUsersTask(LeaderboardActivity a) {
			attach(a);
		}
		
		@Override
		protected void onPreExecute() {
		}
		
		@Override
	    protected void onProgressUpdate(User... u) {
			ref.get().updateAsyncTaskProgress(u[0]);
	    }
		
		@Override
		protected Boolean doInBackground(Void...voids) {
			JSONArray array = JsonHelper.getJsonArrayFromUrl(GET_USERS_URL);
			if (array != null) { 
				try {
					for (int i = 0; i < array.length(); ++i) { 
						publishProgress(
							new User.Builder(
								// required parameters
								array.getJSONObject(i).getInt("users_tbl_id"),
								array.getJSONObject(i).getString("users_tbl_username"),
								array.getJSONObject(i).getString("users_tbl_password"))
									// optional parameters
									.challengesDone(array.getJSONObject(i).getInt("users_tbl_challenges_done"))
									.placesVisited(array.getJSONObject(i).getInt("users_tbl_places_visited"))
									.points(array.getJSONObject(i).getInt("users_tbl_points"))
									.rank(array.getJSONObject(i).getInt("users_tbl_rank"))
										.build());
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFriendTask.doInBackGround(Void ...voids) : ", "JSON error parsing data" + e.toString());
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

		public void attach(LeaderboardActivity a) {
			ref = new WeakReference<LeaderboardActivity>(a);
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
    public void onPause() {
		Log.v(TAG, "I'm paused!");
        super.onPause();
	}
	
	@Override
    public void onDestroy() {
		Log.v(TAG, "I'm destroyed!");
        super.onDestroy();
	}

	public void updateAsyncTaskProgress(User u) {
		userList.add(u);
		adapter.notifyDataSetChanged();
	}
}