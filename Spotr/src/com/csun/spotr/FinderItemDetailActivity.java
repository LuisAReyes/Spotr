package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import com.csun.spotr.adapter.FinderAdditionalItemImageAdapter;
import com.csun.spotr.core.adapter_item.SeekingItem;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Gallery;

public class FinderItemDetailActivity extends Activity {
	private static final String TAG = "(SeekingItemDetailActivity)";
	private static final String GET_FINDER_ADDITIONAL_IMAGES_URL = "http://107.22.209.62/android/get_finder_additional_images.php";
	private List<String> items = new ArrayList<String>();
	private FinderAdditionalItemImageAdapter adapter;
	private int finderId;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finder_item_detail);
		
		finderId = getIntent().getExtras().getInt("finder_id");
		adapter = new FinderAdditionalItemImageAdapter(FinderItemDetailActivity.this, items);
		Gallery ga = (Gallery) findViewById(R.id.finder_item_detail_xml_gallery);
		ga.setAdapter(adapter);
		
		new GetFindersTask().execute();
	}
	
	private class GetFindersTask extends AsyncTask<Integer, String, Boolean> {
		private List<NameValuePair> finderData = new ArrayList<NameValuePair>();
		private ProgressDialog progressDialog = null;
		private JSONArray jsonArray = null;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(FinderItemDetailActivity.this);
			progressDialog.setMessage("Loading items...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(String... urls) {
			items.add(urls[0]);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected Boolean doInBackground(Integer... offsets) {
			finderData.add(new BasicNameValuePair("finder_id", Integer.toString(finderId)));
			jsonArray = JsonHelper.getJsonArrayFromUrlWithData(GET_FINDER_ADDITIONAL_IMAGES_URL, finderData);
			if (jsonArray != null) {
				try {
					for (int i = 0; i < jsonArray.length(); ++i) {
						publishProgress(jsonArray.getJSONObject(i).getString("finder_images_tbl_url"));
					}
				}
				catch (JSONException e) {
					Log.e(TAG + "GetFindersTask.doInBackGround(Integer... offsets) : ", "JSON error parsing data" + e.toString());
				}
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result == false) {
				AlertDialog dialogMessage = new AlertDialog.Builder(FinderItemDetailActivity.this).create();
				dialogMessage.setTitle("Hello " + CurrentUser.getCurrentUser().getUsername());
				dialogMessage.setMessage("No items");
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
