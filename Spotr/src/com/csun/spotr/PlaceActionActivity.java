package com.csun.spotr;

import java.util.ArrayList;
import java.util.List;

import com.csun.spotr.core.CurrentUser;
import com.csun.spotr.core.PlaceActionItem;
import com.csun.spotr.gui.PlaceActionItemAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PlaceActionActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		CurrentUser.setCurrentUser(1, "hello", "android");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_action);
		ArrayList<PlaceActionItem> items = new ArrayList<PlaceActionItem>();
		items.add(new PlaceActionItem("Check In", "Check In", "+1", R.drawable.adium));
		items.add(new PlaceActionItem("Poke someone", "Poke someone", "+1", R.drawable.adium));
		items.add(new PlaceActionItem("Say something", "Say something", "+1", R.drawable.adium));
		items.add(new PlaceActionItem("Snap a pictrue", "Snap a picture", "+1", R.drawable.adium));
		items.add(new PlaceActionItem("Create a challenge", "Create a challenge", "+1", R.drawable.adium));
		ListView actionListView = (ListView) findViewById(R.id.place_action_xml_listview_actions);
		PlaceActionItemAdapter adapter = new PlaceActionItemAdapter(this, items);
		// set adapter
		actionListView.setAdapter(adapter);
		actionListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					Animation animation = new TranslateAnimation(0, 500,0, 0);
					animation.setDuration(1000);
					TextView tv = (TextView) view.findViewById(R.id.place_action_item_xml_textview_title);
					tv.startAnimation(animation);
					tv.setBackgroundColor(android.graphics.Color.GREEN);
					tv.setText(CurrentUser.getCurrentUser().getUsername());
				}
				else if (position == 1) {

				}
				else if (position == 2) {
					
				}
				else if (position == 3) {
					
				}
				else { // position == 4
					
				}
			}
		});
	}
}
