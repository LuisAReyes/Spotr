package com.csun.spotr.gui;

import java.util.List;
import java.util.Vector;

import com.csun.spotr.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.csun.spotr.core.Place;
import com.csun.spotr.helper.DownloadImageHelper;

public class PlaceItemAdapter extends BaseAdapter {
	private Activity 		context;
	private List<Place>	    places;
	private boolean 		notifyChanged = false;

	public PlaceItemAdapter(Activity context, List<Place> places) {
		super();
		this.context = context;
		this.places = places;
	}

	public int getCount() {
		return places.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public static class ItemViewHolder {
		TextView nameTextView;
		TextView typesTextView;
		TextView ratingTextView;
		ImageView mapIconImageView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.place_item, null);
			holder = new ItemViewHolder();
			holder.nameTextView = (TextView) convertView.findViewById(R.id.place_item_xml_textview_name);
			holder.typesTextView = (TextView) convertView.findViewById(R.id.place_item_xml_textview_address);
			holder.ratingTextView = (TextView) convertView.findViewById(R.id.place_item_xml_textview_rating);
			holder.mapIconImageView = (ImageView) convertView.findViewById(R.id.place_item_xml_imageview_location_icon);

			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.nameTextView.setText(places.get(position).getName());
		holder.typesTextView.setText(places.get(position).getAddress());
		holder.ratingTextView.setText(Integer.toString(places.get(position).getRating()));
		holder.mapIconImageView.setImageBitmap(DownloadImageHelper.downloadImage(places.get(position).getIconUrl()));
		return convertView;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		notifyChanged = true;
	}

	/*
	public android.widget.Filter getFilter() {
		return new android.widget.Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				if (constraint != null && constraint.toString().length() > 0) {

				}
				else {
				}
				return results;
			}
		};
	}
	*/
}