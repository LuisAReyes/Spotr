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
	
	//Menu Button Selections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.options_menu_xml_item_setting_Icon :
			Intent i = new Intent("com.csun.spotr.SettingsActivity");
			startActivity(i);
			break;
		case R.id.options_menu_xml_item_logout_Icon :
			Intent k = new Intent("com.csun.spotr.LoginActivity");
			startActivity(k);
			break;
		}
		return true;
	}
	
	
}
