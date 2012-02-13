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
		return null;
	}
}