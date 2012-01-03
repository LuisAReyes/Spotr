package com.csun.spotr.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.csun.spotr.R;
import com.csun.spotr.core.PlaceLog;
import com.csun.spotr.helper.DownloadImageHelper;

public class PlaceActivityItemAdapter extends BaseAdapter {
	private List<PlaceLog> items;
	private Activity context;
	
	public PlaceActivityItemAdapter(Activity context, List<PlaceLog> items) {
		this.context = context;
		this.items = items;
	}

	public int getCount() {
		return items.size();
	}
	
	public Object getItem(int position) {
		return items.get(position);
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	// default fields for every activity (PlaceLog)
	public static class ItemViewHolder {
		TableLayout  table;
		ImageView imageViewUserPicture;
		TextView textViewUsername;
		TextView textViewWhatUserDo;
		TextView textViewTime;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(R.layout.place_activity_item, null);
		// create holder
		holder = new ItemViewHolder();
		// default field
		holder.table = (TableLayout) convertView.findViewById(R.id.place_activity_item_xml_tablelayout_table);
		holder.imageViewUserPicture = (ImageView) convertView.findViewById(R.id.place_activity_item_xml_imageview_user_picture);
		holder.textViewUsername = (TextView) convertView.findViewById(R.id.place_activity_item_xml_textview_username);
		holder.textViewWhatUserDo = (TextView) convertView.findViewById(R.id.place_activity_item_xml_textview_what_user_do);
		holder.textViewTime = (TextView) convertView.findViewById(R.id.place_activity_item_xml_textview_at_what_time);
		convertView.setTag(holder);
		
		/*
		 * TODO: Group user' tasks together to save this download image task
		 */
		holder.imageViewUserPicture.setImageDrawable(items.get(position).getUserPictureDrawable());
		holder.textViewUsername.setText(items.get(position).getUsername());
		holder.textViewTime.setText("@" + items.get(position).getTime());
		
		if (items.get(position).getChallengeType().equals("CHECK_IN")) {
			holder.textViewWhatUserDo.setText("has checked in.");
		}
		else if (items.get(position).getChallengeType().equals("SNAP_PICTURE")) {
			holder.textViewWhatUserDo.setText("has snap a picture.");
			/*
			 * Add a picture box
			 */
			TableRow row = new TableRow(context);	
			ImageView pictureBox = new ImageView(context);
			ImageView dummy = new ImageView(context);
			
			pictureBox.setImageDrawable(items.get(position).getSnapPictureDrawable());
	    	// TableRow.LayoutParams textLayoutParams = new TableRow.LayoutParams();
	    	// textLayoutParams.setMargins(100, 0, 0, 0);
	    	// row.addView(pictureBox, textLayoutParams);
			row.addView(dummy);
			row.addView(pictureBox);
	    	holder.table.addView(row);
		}
		else if (items.get(position).getChallengeType().equals("WRITE_ON_WALL")) {
			holder.textViewWhatUserDo.setText("has written a message on wall.");
			/*
			 * Add a message box
			 */
			TableRow row = new TableRow(context);	
			TextView messageBox = new TextView(context);
			messageBox.setSingleLine(false);
			messageBox.setMaxLines(5);
			ImageView dummy = new ImageView(context);
			messageBox.setText(items.get(position).getComment());
			// TableRow.LayoutParams textLayoutParams = new TableRow.LayoutParams();
	    	// textLayoutParams.setMargins(100, 0, 0, 0);
	    	// row.addView(messageBox, textLayoutParams);
			row.addView(dummy);
			row.addView(messageBox);
	    	holder.table.addView(row);
	
		}
		else if (items.get(position).getChallengeType().equals("QUESTION_ANSWER")) {
			holder.textViewWhatUserDo.setText("has answered a question.");
		}
		else { // Challenge.Type.OTHER
			holder.textViewWhatUserDo.setText("has done some other challenges.");
		}
		
		return convertView;
	}
}