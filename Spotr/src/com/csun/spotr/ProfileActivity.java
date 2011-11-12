package com.csun.spotr;

import java.util.ArrayList;

import com.csun.spotr.gui.ProfileItemAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ProfileActivity extends Activity implements OnItemClickListener {

	private ListView mListView;
	private ProfileItemAdapter mAdapter;

	private static final String mHeaders[] = { "Name", "E-mail", "Level", "Last seen", "Recently completed quests" };
	private static final String mBodies[] = { "Zach Duvall", "zdduvall@gmail.com", "18", "CSU Northridge", "McDonalds\nAttend COMP 490\nFinish ProfileActivity prototype for Sprint 3" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);

		mListView = (ListView) findViewById(R.id.profile_xml_listview_profile);
		mAdapter = new ProfileItemAdapter(this, mHeaders, mBodies);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}
}
