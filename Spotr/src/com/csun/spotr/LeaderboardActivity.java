package com.csun.spotr;

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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.Button;

import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.core.User;
import com.csun.spotr.gui.FriendListMainItemAdapter;
import com.csun.spotr.gui.LeaderboardItemAdapter;
import com.csun.spotr.helper.JsonHelper;

public class LeaderboardActivity extends Activity {
	private static final String TAG = "(LeaderboardActivity)";
	private static final String GET_USERS_URL = "http://107.22.209.62/android/get_users.php";
	private ListView list = null;
	private LeaderboardItemAdapter adapter = null;
	private List<User> userList = new ArrayList<User>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);
		// initialize list view
		list = (ListView) findViewById(R.id.leaderboard_xml_listview_users);
		adapter = new LeaderboardItemAdapter(LeaderboardActivity.this, userList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// handle click 
			}
		});
		new GetUsersTask().execute();
		
		Button buttonWhere = (Button) findViewById(R.id.leaderboard_xml_button_where_am_i);
		buttonWhere.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				// run new task
				list.post(new Runnable() {
					public void run() {
						list.setSelection(CurrentUser.getSelectedPosition());
						list.getChildAt(CurrentUser.getSelectedPosition()).setBackgroundColor(Color.RED);
						v.setEnabled(false);
						v.setBackgroundColor(color.transparent);
				}});
			}
		});
	}
	
	private class GetUsersTask extends AsyncTask<Void, User, Boolean> {
		private  ProgressDialog progressDialog = null;
		@Override
		protected void onPreExecute() {
			// display waiting dialog
			progressDialog = new ProgressDialog(LeaderboardActivity.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}
		
		@Override
	    protected void onProgressUpdate(User... users) {
			progressDialog.dismiss();
			userList.add(users[0]);
			adapter.notifyDataSetChanged();
			// adapter.notifyDataSetInvalidated();
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
			progressDialog.dismiss();
			

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
}