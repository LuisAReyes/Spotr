package com.csun.spotr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.csun.spotr.adapter.ChallengeItemAdapter;

public class ChallengeActivity extends Activity {
	private static final String TAG = "[ChallengeActivity]";
	private ListView challengeListView;
	private ChallengeItemAdapter challengeItems;

	private static final String headers[] = { "Challenge 1", "Challenge 2", "Challenge 3", "Challenge 4", };

	private static final String bodies[] = { "Description of challenge 1", "Description of challenge 2", "Description of challenge 3", "Description of challenge 4" };

	private static final String rating[] = { "1", "2", "3", "4" };

	private static final boolean flags[] = { false, false, false, true };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.challenge);
		challengeListView = (ListView) findViewById(R.id.challenge_xml_listview_challenge);
		challengeItems = new ChallengeItemAdapter(this, headers, bodies, rating, flags);
		challengeListView.setAdapter(challengeItems);
		challengeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
	}
}