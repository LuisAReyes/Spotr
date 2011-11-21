/**
 * @author Aleksandr Rozenman
 * @author Adam Brakel
 */

package com.csun.spotr;

import com.csun.spotr.gui.ChallengeInfoAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ChallengeInfoActivity extends Activity {
	private ChallengeInfoAdapter challengeInfo;
	private ListView challengeInfoListView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.challenge_info);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		challengeInfoListView = new ListView(this);
		challengeInfo = new ChallengeInfoAdapter(this);
		challengeInfoListView.setAdapter(challengeInfo);
		challengeInfoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
	}
}
