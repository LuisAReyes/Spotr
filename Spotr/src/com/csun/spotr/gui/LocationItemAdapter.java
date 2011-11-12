package com.csun.spotr.gui;

import com.csun.spotr.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocationItemAdapter extends BaseAdapter {
	private Activity context;
	private String[] names;
	private Bitmap[] iconBitmaps;
	private String[] categories;
	private String[] ratings;

	public LocationItemAdapter(Activity activityContext, String[] names, Bitmap[] iconBitmaps, String[] categories, String[] ratings) {
		super();
		this.context = activityContext;
		this.names = names;
		this.iconBitmaps = iconBitmaps;
		this.categories = categories;
		this.ratings = ratings;
	}

	public int getCount() {
		return names.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public static class ItemViewHolder {
		TextView nameTextView;
		TextView categoryTextView;
		TextView ratingTextView;
		ImageView mapIconImageView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.location_item, null);
			holder = new ItemViewHolder();
			holder.nameTextView = (TextView) convertView.findViewById(R.id.location_item_xml_textview_name);
			holder.categoryTextView = (TextView) convertView.findViewById(R.id.location_item_xml_textview_category);
			holder.ratingTextView = (TextView) convertView.findViewById(R.id.location_item_xml_textview_rating);
			holder.mapIconImageView = (ImageView) convertView.findViewById(R.id.location_item_xml_imageview_location_icon);
			
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.nameTextView.setText(names[position]);
		holder.categoryTextView.setText(categories[position]);
		holder.ratingTextView.setText(ratings[position]);
		holder.mapIconImageView.setImageBitmap(iconBitmaps[position]);
		
		return convertView;
	}
}