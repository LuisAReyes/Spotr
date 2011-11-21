/**
 * @author Aleksandr Rozenman
 * @author Adam Brakel
 * @author: Chan Nguyen
 */

package com.csun.spotr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.csun.spotr.gui.ChallengeItemAdapter;

public class ChallengeActivity extends Activity {
	private ListView challengeListView;
	private ChallengeItemAdapter challengeItems;

	private static final String headers[] = { "Challenge 1", "Challenge 2", "Challenge 3", "Challenge 4", "Challenge 5", "Challenge 6", "Challenge 7", "Challenge 8", "Challenge 9", "Challenge 10", "Challenge 11", "Challenge 12", "Challenge 13", "Challenge 14", "Challenge 15", };

	private static final String bodies[] = { "Description of challenge 1", "Description of challenge 2", "Description of challenge 3", "Description of challenge 4", "Description of challenge 5", "Description of challenge 6", "Description of challenge 7", "Description of challenge 8", "Description of challenge 9", "Description of challenge 10", "Description of challenge 11", "Description of challenge 12", "Description of challenge 13", "Description of challenge 14", "Description of challenge 15", };

	private static final String rating[] = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" };

	private static final boolean flags[] = { false, false, false, true, false, false, false, false, false, false, false, false, false, false, false };

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