package com.csun.spotr.adapter;

import java.util.List;

import com.csun.spotr.core.adapter_item.PlaceItem;
import com.csun.spotr.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class PlaceItemAdapter extends BaseAdapter {
	private Context context;
	private List<PlaceItem> items;
	private static LayoutInflater inflater;
	private ItemViewHolder holder;
	
	public PlaceItemAdapter(Context context, List<PlaceItem> items) {
		super();
		this.context = context.getApplicationContext();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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