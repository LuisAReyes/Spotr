package com.csun.spotr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.csun.spotr.adapter.WeaponAdapter;
import com.csun.spotr.core.Weapon;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.skeleton.IActivityProgressUpdate;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.JsonHelper;

/**
 * Description:
 * 		Display user's weapons
 */
public class WeaponActivity 
	extends Activity 
		implements IActivityProgressUpdate<Weapon> {
	
	private static final 	String 					TAG = "(WeaponActivity)";
	private static final 	String 					GET_WEAPON_URL = "http://107.22.209.62/android/get_weapons.php";
	
	private 			 	ListView 				listview;
	private 				WeaponAdapter 			adapter;
	private					List<Weapon> 			weaponList = new ArrayList<Weapon>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weapon);
		
		listview = (ListView) findViewById(R.id.weapon_xml_listview_weapons);
		adapter = new WeaponAdapter(this, weaponList);
		listview.setAdapter(adapter);
		
		new GetWeaponTask(this).execute();
	}

	
	private static class GetWeaponTask 
		extends AsyncTask<Integer, Weapon, Boolean> 
			implements IAsyncTask<WeaponActivity> {
		
		private List<NameValuePair> clientData = new ArrayList<NameValuePair>();
		private WeakReference<WeaponActivity> ref;
		private JSONArray array;

		public GetWeaponTask(WeaponActivity a) {
			attach(a);
		}
		
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Weapon... w) {
			ref.get().updateAsyncTaskProgress(w[0]);
		}

		@Override
		protected Boolean doInBackground(Integer... offsets) {
			// send user id
			clientData.add(new BasicNameValuePair("id", Integer.toString(CurrentUser.getCurrentUser().getId())));
			// retrieve data from server
			array = JsonHelper.getJsonArrayFromUrlWithData(GET_WEAPON_URL, clientData);
			if (array != null) {
				try {
					for (int i = 0; i < array.length(); ++i) {
						publishProgress(
							new Weapon(
								array.getJSONObject(i).getInt("weapon_tbl_id"),
								array.getJSONObject(i).getString("weapon_tbl_title"),
								array.getJSONObject(i).getString("weapon_tbl_icon_url"),
								array.getJSONObject(i).getDouble("weapon_tbl_point_percentage"),
								array.getJSONObject(i).getInt("users_weapon_tbl_times_left")));
							
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetWeaponTask.doInBackGround(Integer... offsets) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
		}
		
		public void attach(WeaponActivity a) {
			ref = new WeakReference<WeaponActivity>(a);
		}
		
		public void detach() {
			ref.clear();
		}
	}


	public void updateAsyncTaskProgress(Weapon u) {
		weaponList.add(u);
		adapter.notifyDataSetChanged();
	}
}
