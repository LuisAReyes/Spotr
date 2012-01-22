package com.csun.spotr.adapter;

import java.util.List;

import com.csun.spotr.core.adapter_item.PlaceItem;
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

import com.csun.spotr.core.adapter_item.PlaceItem;
import com.csun.spotr.util.ImageHelper;

public class PlaceItemAdapter extends BaseAdapter {
	private Activity context;
	private List<PlaceItem> items;

	public PlaceItemAdapter(Activity context, List<PlaceItem> items) {
		super();
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
		TextView nameTextView;
		TextView typesTextView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.place_item, null);
			holder = new ItemViewHolder();
			holder.nameTextView = (TextView) convertView.findViewById(R.id.place_item_xml_textview_name);
			holder.typesTextView = (TextView) convertView.findViewById(R.id.place_item_xml_textview_address);

			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.nameTextView.setText(items.get(position).getName());
		holder.typesTextView.setText(items.get(position).getAddress());
		return convertView;
	}
}