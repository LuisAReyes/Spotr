package com.csun.spotr.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import com.csun.spotr.core.adapter_item.PlaceFeedItem;
import com.csun.spotr.util.ImageLoader;

public class PlaceFeedItemAdapter extends BaseAdapter {
	private List<PlaceFeedItem> items;
	private Context context;
	private static LayoutInflater inflater;
	public ImageLoader imageLoader;
	private ItemViewHolder holder;
	
	public PlaceFeedItemAdapter(Context context, List<PlaceFeedItem> items) {
		this.context = context.getApplicationContext();
		this.items = items;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context.getApplicationContext());
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
	
	public static class ItemViewHolder {
		TableRow rowCheckIn;
		TableRow rowWriteOnWall;
		TableRow rowSnapPictureTitle;
		TableRow rowSnapPictureBox;
		
		// default
		ImageView imageViewUserPicture;
		TextView textViewUsername;
		TextView textViewWhatUserDo;
		TextView textViewWhere;
		TextView textViewTime;
		
		TextView textViewCheckIn;
		// custom
		TextView textViewSnapPictureTitle;
		ImageView imageViewSnapPictureBox;
		
		TextView textViewWriteOnWallTitle;
		TextView textViewWriteOnWallDescription;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.friend_list_feed_item, null);
			holder = new ItemViewHolder();
			holder.imageViewUserPicture = (ImageView) convertView.findViewById(R.id.friend_list_feed_item_xml_imageview_user_picture);
			holder.textViewUsername = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_username);
			holder.textViewWhere = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_where);
			holder.textViewTime = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_time);
			
			holder.textViewCheckIn = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_checkin);
	
			holder.textViewSnapPictureTitle = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_snap_picture_title);
			holder.imageViewSnapPictureBox = (ImageView) convertView.findViewById(R.id.friend_list_feed_item_xml_imageview_snap_picture_box);
			
			holder.textViewWriteOnWallTitle = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_write_on_wall_title);
			holder.textViewWriteOnWallDescription = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_write_on_wall_description);
			
			holder.rowCheckIn = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_checkin);
			holder.rowWriteOnWall = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_write_on_wall);
			holder.rowSnapPictureTitle = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_snap_picture_title);
			holder.rowSnapPictureBox = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_snap_picture_box);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}
		
		// default set
		imageLoader.displayImage(items.get(position).getUserPictureUrl(), holder.imageViewUserPicture);
		holder.textViewUsername.setText(items.get(position).getUsername());
		holder.textViewTime.setText(" @: " + items.get(position).getTime());
		
		if (items.get(position).getChallengeType() == Challenge.Type.CHECK_IN) {
			holder.rowCheckIn.setVisibility(View.VISIBLE);
			holder.rowSnapPictureTitle.setVisibility(View.GONE);
			holder.rowSnapPictureBox.setVisibility(View.GONE);
			holder.rowWriteOnWall.setVisibility(View.GONE);
			holder.textViewCheckIn.setText("has checked in.");
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.SNAP_PICTURE) {
			holder.rowSnapPictureTitle.setVisibility(View.VISIBLE);
			holder.rowSnapPictureBox.setVisibility(View.VISIBLE);
			holder.rowCheckIn.setVisibility(View.GONE);
			holder.rowWriteOnWall.setVisibility(View.GONE);
			holder.textViewSnapPictureTitle.setText("snap a picture");
			imageLoader.displayImage(items.get(position).getSnapPictureUrl(), holder.imageViewSnapPictureBox);
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.WRITE_ON_WALL) {
			holder.rowWriteOnWall.setVisibility(View.VISIBLE);
			holder.rowSnapPictureTitle.setVisibility(View.GONE);
			holder.rowSnapPictureBox.setVisibility(View.GONE);
			holder.rowCheckIn.setVisibility(View.GONE);
			holder.textViewWriteOnWallTitle.setText("has written on wall: ");
			holder.textViewWriteOnWallDescription.setText(items.get(position).getComment());
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.QUESTION_ANSWER) {
			
		}
		else { // Challenge.Type.OTHER
		}
		
		return convertView;
	}
}