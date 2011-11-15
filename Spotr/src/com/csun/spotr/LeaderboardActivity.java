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
import android.widget.Toast;


import com.csun.spotr.gui.LeaderboardItemAdapter;

public class LeaderboardActivity extends Activity {
	private ListView leaderboardListView;
	private LeaderboardItemAdapter leaderboardItems;

	private static final int FINAL = 20;

	private static final String username[] = new String[FINAL];
	private static final String statistics[] = new String[FINAL];
	private static final String rank[] = new String[FINAL];
	private static final int imageId[] = new int[FINAL];
	
	private ListView userView;
	private LeaderboardItemAdapter userItem;
	
	private static final String user[] = {"Megaman"};
	private static final String user_stat[] = {"Super Fighting Robot"};
	private static final String user_rank[] = {"27"}; 
	private static final int user_imageId[] = { R.drawable.megaman };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.leaderboard);
		for (int i = 0; i < FINAL; i++) {
			username[i] = "username" + i;
			statistics[i] = "Status" + i;
			rank[i] = "" + (i + 1);
			imageId[i] = R.drawable.leaderboard;
		}
		
		////////////////////////////////////////////////////////////////////
		userView = (ListView) findViewById(R.id.leaderboard_xml_user_view);
		userItem = new LeaderboardItemAdapter(this,user, user_stat, user_rank, user_imageId);
		userView.setAdapter(userItem);
		userView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				//-- Edgardo C. -- //
				// Added a makeText generator to show name.
				// Will later implement to show score, possibly?
				Toast.makeText(getBaseContext(),
				user[position] + "'s Score Goes Here", Toast.LENGTH_SHORT).show();
						
			}
		});
		
		///////////////////////////////////////////////////////////////////////////
		

		leaderboardListView = (ListView) findViewById(R.id.leaderboard_xml_listview);
		leaderboardItems = new LeaderboardItemAdapter(this, username, statistics, rank, imageId);
		leaderboardListView.setAdapter(leaderboardItems);
		leaderboardListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}
}