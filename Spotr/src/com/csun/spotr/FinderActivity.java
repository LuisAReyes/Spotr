package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.csun.spotr.adapter.SeekingItemAdapter;
import com.csun.spotr.adapter.UserItemAdapter;
import com.csun.spotr.core.adapter_item.SeekingItem;
import com.csun.spotr.core.adapter_item.UserItem;
import com.csun.spotr.custom_gui.DraggableGridView;
import com.csun.spotr.singleton.CurrentUser;
import com.csun.spotr.util.JsonHelper;

public class FinderActivity extends Activity {
	private static final String TAG = "(FinderActivity)";
	private static final String GET_FINDERS_URL = "http://107.22.209.62/android/get_finders.php";
	private List<SeekingItem> items;
	private GridView gridview;
	private SeekingItemAdapter adapter;
	private Button buttonCreateItem;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finder);

		buttonCreateItem = (Button) findViewById(R.id.finder_xml_button);
		items = new ArrayList<SeekingItem>();
		gridview = (GridView) findViewById(R.id.finder_xml_gridview);
		adapter = new SeekingItemAdapter(this, items);
		gridview.setAdapter(adapter);
		
		buttonCreateItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CreateLostItemActivity.class);
				startActivity(intent);
			}
		});
		
		new GetFindersTask().execute();
	}

	private class GetFindersTask extends AsyncTask<Integer, SeekingItem, Boolean> {
		private ProgressDialog progressDialog = null;
		private JSONArray jsonArray = null;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(FinderActivity.this);
			progressDialog.setMessage("Loading items...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onProgressUpdate(SeekingItem... anItems) {
			items.add(anItems[0]);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected Boolean doInBackground(Integer... offsets) {
			jsonArray = JsonHelper.getJsonArrayFromUrl(GET_FINDERS_URL);
			if (jsonArray != null) {
				try {
					for (int i = 0; i < jsonArray.length(); ++i) {
						publishProgress(
							new SeekingItem(
								jsonArray.getJSONObject(i).getInt("finder_tbl_id"), 
								jsonArray.getJSONObject(i).getString("finder_tbl_name"), 
								jsonArray.getJSONObject(i).getString("finder_tbl_image_url")));
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
				AlertDialog dialogMessage = new AlertDialog.Builder(FinderActivity.this).create();
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
