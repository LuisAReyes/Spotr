package com.csun.spotr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class FriendListActivity extends Activity {
	String[] friendsList;
	ArrayAdapter<String> adapter;
	ListView mListView;
	EditText mSearchBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		friendsList = getResources().getStringArray(R.array.friends_array);
		mListView = (ListView) findViewById(R.id.friend_list_xml_listview_friends);
		mSearchBar = (EditText) findViewById(R.id.friend_list_xml_autocompletetextview_search_box);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, friendsList);
		mListView.setAdapter(adapter);
	}

	public void onListItemClick(ListView listView, View V, int position, long id) {
		listView.setItemChecked(position, listView.isItemChecked(position));
		Toast.makeText(this, "This is your friend " + friendsList[position], Toast.LENGTH_SHORT).show();
	}
}