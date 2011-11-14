package com.csun.spotr;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.csun.spotr.gui.LeaderboardItemAdapter;

public class LeaderboardActivity extends Activity {
	private ListView leaderboardListView;
	private LeaderboardItemAdapter leaderboardItems;

	private static final int FINAL = 20;

	private static final String username[] = new String[FINAL];
	private static final String statistics[] = new String[FINAL];
	private static final String rank[] = new String[FINAL];
	private static final int imageId[] = new int[FINAL];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leaderboard);

		for (int i = 0; i < FINAL; i++) {
			username[i] = "username" + i;
			statistics[i] = "Status" + i;
			rank[i] = "" + (i + 1);
			imageId[i] = R.drawable.leaderboard2;
		}
		
		leaderboardListView = (ListView) findViewById(R.id.leaderboard_xml_listview);
		leaderboardItems = new LeaderboardItemAdapter(this, username, statistics, rank, imageId);
		leaderboardListView.setAdapter(leaderboardItems);
		leaderboardListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}
}