package com.csun.spotr;

import java.lang.ref.WeakReference;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.core.Treasure;
import com.csun.spotr.skeleton.IActivityProgressUpdate;
import com.csun.spotr.skeleton.IAsyncTask;
import com.csun.spotr.util.ImageLoader;
import com.csun.spotr.util.JsonHelper;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;


/**
 * Description:
 * 		Generate a random treasure from server and return to user
 */
public class TreasureActivity 
	extends Activity 
		implements IActivityProgressUpdate<Treasure> {
	
	private static final 	String 			TAG = "(TreasureActivity)";
	private static final 	String 			GET_RANDOM_TREASURE_URL = "http://107.22.209.62/android/get_treasure.php";
	
	private 				TextView 		textViewName;
	private 				TextView 		textViewCompany;
	private 				TextView 		textViewExpirationDate;
	private 				TextView 		textViewBarcode;
	private 				ImageView 		imageViewIcon;
	private 				ImageLoader 	imageLoader;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.treasure);
	
		ViewFlipper flipper = (ViewFlipper) findViewById(R.id.treasure_xml_viewflipper_flipper);
		flipper.startFlipping();
		flipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
		flipper.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
		
		textViewName = (TextView) findViewById(R.id.treasure_xml_textview_name);
		textViewCompany = (TextView) findViewById(R.id.treasure_xml_textview_company);
		textViewExpirationDate = (TextView) findViewById(R.id.treasure_xml_textview_expiration_date);
		textViewBarcode = (TextView) findViewById(R.id.treasure_xml_textview_barcode);
		imageViewIcon = (ImageView) findViewById(R.id.treasure_xml_imageview_icon);
		
		imageLoader = new ImageLoader(getApplicationContext());
		
		// get treasure 
		new GetRandomTreasureTask(this).execute();
	}
	
	private class GetRandomTreasureTask 
		extends AsyncTask<Void, Treasure, Boolean> 
			implements IAsyncTask<TreasureActivity> {
		
		private WeakReference<TreasureActivity> ref;

		public GetRandomTreasureTask(TreasureActivity a) {
			attach(a);
		}

		@Override
		protected void onPreExecute() {
		
		}

		@Override
		protected void onProgressUpdate(Treasure... t) {
			ref.get().updateAsyncTaskProgress(t[0]);
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			JSONArray array = JsonHelper.getJsonArrayFromUrl(GET_RANDOM_TREASURE_URL);
			if (array != null) {
				try {
					publishProgress(
						new Treasure(
							array.getJSONObject(0).getInt("treasure_tbl_id"),
							array.getJSONObject(0).getString("treasure_tbl_name"),
							array.getJSONObject(0).getString("treasure_tbl_icon_url"),
							array.getJSONObject(0).getString("treasure_tbl_type"),
							array.getJSONObject(0).getString("treasure_tbl_code"),
							array.getJSONObject(0).getString("treasure_tbl_company"),
							array.getJSONObject(0).getString("treasure_tbl_expiration_date"))
						);
				}
				catch (JSONException e) {
					Log.e(TAG + "GetRandomTreasureTask.doInBackGround(Void... voids) : ", "JSON error parsing data" + e.toString());
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

		public void attach(TreasureActivity a) {
			ref = new WeakReference<TreasureActivity>(a);
		}

		public void detach() {
			ref.clear();
		}
	}
	
	public void updateAsyncTaskProgress(Treasure t) {
		textViewName.setText(t.getName());
		textViewCompany.setText(t.getCompany());
		textViewExpirationDate.setText(t.getExpirationDate());
		textViewBarcode.setText(t.getCode());
		imageLoader.displayImage(t.getIconUrl(), imageViewIcon);
	}
}