package com.csun.spotr.gui;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.csun.spotr.R;
import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.FriendFeed;

public class FriendFeedItemAdapter extends BaseAdapter {
	private List<FriendFeed> items;
	private Activity context;
	
	public FriendFeedItemAdapter(Activity context, List<FriendFeed> items) {
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
		TextView textViewWhere;
		TextView textViewTime;
		ImageView imageViewSnapPictureBox;
		TextView textViewWriteOnWallMessageBox;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		convertView = inflater.inflate(R.layout.friend_list_feed_item, null);
		// create holder
		holder = new ItemViewHolder();
		// default field
		holder.table = (TableLayout) convertView.findViewById(R.id.friend_list_feed_item_xml_tablelayout_table);
		holder.imageViewUserPicture = (ImageView) convertView.findViewById(R.id.friend_list_feed_item_xml_imageview_user_picture);
		holder.textViewUsername = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_username);
		holder.textViewWhatUserDo = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_what_user_do);
		holder.textViewWhere = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_where);
		holder.textViewTime = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_at_what_time);
		convertView.setTag(holder);
		
		holder.imageViewUserPicture.setImageDrawable(items.get(position).getFriendPictureDrawable());
		holder.textViewUsername.setText(items.get(position).getFriendName());
		holder.textViewWhere.setText("at " + items.get(position).getPlaceName());
		holder.textViewTime.setText("@" + items.get(position).getActivityTime());
		
		if (items.get(position).getChallengeType() == Challenge.Type.CHECK_IN) {
			holder.textViewWhatUserDo.setText("has checked in.");
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.SNAP_PICTURE) {
			holder.textViewWhatUserDo.setText("has snap a picture.");
			// add picture box
			View rowView = inflater.inflate(R.layout.snap_picture_row_item, null);
			holder.imageViewSnapPictureBox = (ImageView) rowView.findViewById(R.id.snap_picture_row_item_xml_imageview_picture);
			holder.imageViewSnapPictureBox.setImageDrawable(items.get(position).getActivitySnapPictureDrawable());
			holder.table.addView(rowView);
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.WRITE_ON_WALL) {
			holder.textViewWhatUserDo.setText("has written a message on wall.");
			// add message box
			View rowView = inflater.inflate(R.layout.write_on_wall_row_item, null);
			holder.textViewWriteOnWallMessageBox = (TextView) rowView.findViewById(R.id.write_on_wall_row_item_xml_textview_wall_message);
			holder.textViewWriteOnWallMessageBox.setText(items.get(position).getActivityComment());
	    	holder.table.addView(rowView);
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.QUESTION_ANSWER) {
			holder.textViewWhatUserDo.setText("has answered a question.");
		}
		else { // Challenge.Type.OTHER
			holder.textViewWhatUserDo.setText("has done some other challenges.");
		}
		
		return convertView;
	}
}