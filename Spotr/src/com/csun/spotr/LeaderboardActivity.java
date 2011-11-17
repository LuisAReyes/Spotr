package com.csun.spotr;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;


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
	private static String megaman_blaster = "1,234,444,700";

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

				showDialog(0);
			}
		});
		
		///////////////////////////////////////////////////////////////////////////
		

		leaderboardListView = (ListView) findViewById(R.id.leaderboard_xml_listview);
		leaderboardItems = new LeaderboardItemAdapter(this, username, statistics, rank, imageId);
		leaderboardListView.setAdapter(leaderboardItems);
		leaderboardListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//showDialog(0);
			}
		});
	}
	
	protected Dialog onCreateDialog(int id) {
//		ImageView image = (ImageView) findViewById(R.drawable.megaman);
  //      Animation hyperspaceJump = 
    //            AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);
      //      image.startAnimation(hyperspaceJump);
		
			switch (id) {
				case 0:
					/*return new AlertDialog.Builder(this)
					.setIcon(R.drawable.leaderboards)
					.setTitle(megaman_blaster)
					.setPositiveButton("RETURN", new
							DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
								{
									Toast.makeText(getBaseContext(),
										"You're getting PWND!", Toast.LENGTH_SHORT).show();
								}
					})
					.create();
					*/
					AlertDialog.Builder builder;
					AlertDialog alertDialog;

					
					LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.leaderboard_dialog_box,
					                               (ViewGroup) findViewById(R.id.layout_root));

					TextView text = (TextView) layout.findViewById(R.id.text);
					text.setText("Hello, this is a custom dialog!");
					ImageView image = (ImageView) layout.findViewById(R.id.image);
					image.setImageResource(R.drawable.leaderboards);

					builder = new AlertDialog.Builder(this);
					builder.setView(layout);
					builder.setPositiveButton("RETURN", new
							DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton)
								{
									Toast.makeText(getBaseContext(),
										"You're getting PWND!", Toast.LENGTH_SHORT).show();
								}
					});
					alertDialog = builder.create();
					return alertDialog;
			}
			return null;
		}
	
}