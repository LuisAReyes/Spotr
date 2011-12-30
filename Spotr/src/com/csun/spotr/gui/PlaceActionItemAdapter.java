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
import android.widget.TextView;

import com.csun.spotr.R;
import com.csun.spotr.core.Challenge;

public class PlaceActionItemAdapter extends BaseAdapter {
	private List<Challenge> items;
	private Activity context;
	
	public PlaceActionItemAdapter(Activity context, List<Challenge> items) {
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
	
	public static class ItemViewHolder {
		TextView   titleTextView;
		TextView   descriptionTextView;
		TextView   pointTextView;
		ImageView  iconImageView;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.place_action_item, null);
			holder = new ItemViewHolder();
			holder.titleTextView = (TextView) convertView.findViewById(R.id.place_action_item_xml_textview_title);
			holder.descriptionTextView = (TextView) convertView.findViewById(R.id.place_action_item_xml_textview_subtitle);
			holder.pointTextView = (TextView) convertView.findViewById(R.id.place_action_item_xml_textview_point);
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.place_action_item_xml_imageview_action_icon);

			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.titleTextView.setText(items.get(position).getName());
		holder.descriptionTextView.setText(items.get(position).getDescription());
		holder.pointTextView.setText(Integer.toString(items.get(position).getPoints()));
		
		if (items.get(position).getType() == Challenge.Type.CHECK_IN)
			holder.iconImageView.setImageResource(R.drawable.adium);
		else if (items.get(position).getType() == Challenge.Type.SNAP_PICTURE) 
			holder.iconImageView.setImageResource(R.drawable.monkey);
		else if (items.get(position).getType() == Challenge.Type.WRITE_ON_WALL)
			holder.iconImageView.setImageResource(R.drawable.circus_car);
		else if (items.get(position).getType() == Challenge.Type.WRITE_ON_WALL)
			holder.iconImageView.setImageResource(R.drawable.elephant);
		else 
			holder.iconImageView.setImageResource(R.drawable.funshine_bear);
		
		return convertView;
	}
}