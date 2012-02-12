package com.csun.spotr;

import com.csun.spotr.custom_gui.DraggableGridView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class RewardActivity extends Activity {
	private static final String TAG = "(RewardActivity)";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward);
		
		 Integer[] thumbIds = {
			R.drawable.newbie, R.drawable.adventurer,
		    R.drawable.explorer, R.drawable.superstar,
		    R.drawable.awards, R.drawable.awards,
		};
		//GridView
	    DraggableGridView gridview = (DraggableGridView) findViewById(R.id.reward_xml_gridview_rewards);
	    
	    for (int i = 0; i < thumbIds.length; ++i) {
		    ImageView img = new ImageView(this);
		    img.setImageDrawable(getResources().getDrawable(thumbIds[i]));
		    img.setPadding(2, 2, 2, 2);
		    gridview.addView(img);
	    }
	    
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	           Intent intent = new Intent(getApplicationContext(), RewardViewActivity.class);
	           startActivity(intent);
	        }
	    });	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.all_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case R.id.options_menu_xml_item_setting_icon:
				intent = new Intent("com.csun.spotr.SettingsActivity");
				startActivity(intent);
				finish();
				break;
			case R.id.options_menu_xml_item_logout_icon:
				SharedPreferences.Editor editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
				editor.clear();
				editor.commit();
				intent = new Intent("com.csun.spotr.LoginActivity");
				startActivity(intent);
				finish();
				break;
			case R.id.options_menu_xml_item_mainmenu_icon:
				intent = new Intent("com.csun.spotr.MainMenuActivity");
				startActivity(intent);
				finish();
				break;
		}
		return true;
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
