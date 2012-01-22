package com.csun.spotr.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.csun.spotr.R;
import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.adapter_item.FriendFeedItem;
import com.csun.spotr.util.ImageLoader;

public class FriendFeedItemAdapter extends BaseAdapter {
	private List<FriendFeedItem> items;
	private Activity context;
	private static LayoutInflater inflater;
	public ImageLoader imageLoader;
	private ItemViewHolder ivh;
	
	public FriendFeedItemAdapter(Activity context, List<FriendFeedItem> items) {
		this.context = context;
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
			ivh = new ItemViewHolder();
			ivh.imageViewUserPicture = (ImageView) convertView.findViewById(R.id.friend_list_feed_item_xml_imageview_user_picture);
			ivh.textViewUsername = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_username);
			ivh.textViewWhere = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_where);
			ivh.textViewTime = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_time);
			
			ivh.textViewCheckIn = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_checkin);
	
			ivh.textViewSnapPictureTitle = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_snap_picture_title);
			ivh.imageViewSnapPictureBox = (ImageView) convertView.findViewById(R.id.friend_list_feed_item_xml_imageview_snap_picture_box);
			
			ivh.textViewWriteOnWallTitle = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_write_on_wall_title);
			ivh.textViewWriteOnWallDescription = (TextView) convertView.findViewById(R.id.friend_list_feed_item_xml_textview_write_on_wall_description);
			
			ivh.rowCheckIn = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_checkin);
			ivh.rowWriteOnWall = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_write_on_wall);
			ivh.rowSnapPictureTitle = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_snap_picture_title);
			ivh.rowSnapPictureBox = (TableRow) convertView.findViewById(R.id.friend_list_feed_item_xml_tablerow_snap_picture_box);
			
			convertView.setTag(ivh);
		}
		else {
			ivh = (ItemViewHolder) convertView.getTag();
		}
		
		// default set
		imageLoader.displayImage(items.get(position).getFriendPictureUrl(), ivh.imageViewUserPicture);
		ivh.textViewUsername.setText(items.get(position).getFriendName());
		ivh.textViewTime.setText(" @: " + items.get(position).getActivityTime());
		ivh.textViewWhere.setText("at " + items.get(position).getPlaceName());
		
		
		if (items.get(position).getChallengeType() == Challenge.Type.CHECK_IN) {
			ivh.rowCheckIn.setVisibility(View.VISIBLE);
			ivh.rowSnapPictureTitle.setVisibility(View.GONE);
			ivh.rowSnapPictureBox.setVisibility(View.GONE);
			ivh.rowWriteOnWall.setVisibility(View.GONE);
			ivh.textViewCheckIn.setText("has checked in.");
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.SNAP_PICTURE) {
			ivh.rowSnapPictureTitle.setVisibility(View.VISIBLE);
			ivh.rowSnapPictureBox.setVisibility(View.VISIBLE);
			ivh.rowCheckIn.setVisibility(View.GONE);
			ivh.rowWriteOnWall.setVisibility(View.GONE);
			ivh.textViewSnapPictureTitle.setText("snap a picture");
			imageLoader.displayImage(items.get(position).getActivitySnapPictureUrl(), ivh.imageViewSnapPictureBox);
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.WRITE_ON_WALL) {
			ivh.rowWriteOnWall.setVisibility(View.VISIBLE);
			ivh.rowSnapPictureTitle.setVisibility(View.GONE);
			ivh.rowSnapPictureBox.setVisibility(View.GONE);
			ivh.rowCheckIn.setVisibility(View.GONE);
			ivh.textViewWriteOnWallTitle.setText("write on wall");
			ivh.textViewWriteOnWallDescription.setText(items.get(position).getActivityComment());
		}
		else if (items.get(position).getChallengeType() == Challenge.Type.QUESTION_ANSWER) {
		}
		else { // Challenge.Type.OTHER
		}
		
		return convertView;
	}
}