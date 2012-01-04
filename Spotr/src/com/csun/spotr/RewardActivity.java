package com.csun.spotr;

import com.csun.spotr.gui.RewardImageAdapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RewardActivity extends Activity {
	private static final String TAG = "[RewardActivity]";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward);
		
		Button btnMainMenu;
		
		//Button for Main Menu
		btnMainMenu = (Button) findViewById(R.id.reward_xml_button_mainmenu);
		btnMainMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent("com.csun.spotr.MainMenuActivity");
				startActivity(i);
			}
		});
		
		//GridView
	    GridView gridview = (GridView) findViewById(R.id.reward_xml_gridView1);
	    gridview.setAdapter(new RewardImageAdapter(this));

	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	           //Toast.makeText(RewardActivity.this, "Reward " + position, Toast.LENGTH_SHORT).show();
	        	
	           Intent intent = new Intent(getApplicationContext(), RewardViewActivity.class);
	           startActivity(intent);

	        }
	    });	
	}
}
