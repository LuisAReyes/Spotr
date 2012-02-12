package com.csun.spotr.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.csun.spotr.R;
import com.csun.spotr.core.adapter_item.PlaceActionItem;

public class UserActivityItemAdapter extends BaseAdapter {
	private List<String> items;
	private Context context;
	private static LayoutInflater inflater;
	private ItemViewHolder holder;

	public UserActivityItemAdapter(Context context, List<String> items) {
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

	private static class ItemViewHolder {
		TextView textViewText;
		TextView textViewText2;
		TableLayout table;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.user_activity_item, null);
			holder = new ItemViewHolder();
			holder.textViewText = (TextView) convertView.findViewById(R.id.user_activity_item_xml_textview_user_name);
			holder.textViewText2 = (TextView) convertView.findViewById(R.id.user_activity_item_xml_textview_user_where);
			holder.table = (TableLayout) convertView.findViewById(R.id.user_activity_item_xml_tablelayout_activity_table);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}
		
		holder.textViewText.setText(items.get(position));
		holder.textViewText2.setText(items.get(position));
		return convertView;
	}
}