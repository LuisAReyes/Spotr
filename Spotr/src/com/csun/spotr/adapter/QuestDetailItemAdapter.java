package com.csun.spotr.adapter;

import java.util.List;

import com.csun.spotr.R;
import com.csun.spotr.core.adapter_item.QuestDetailItem;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuestDetailItemAdapter extends BaseAdapter{
	private Context context;
	private List<QuestDetailItem> items;
	private ItemViewHolder holder;
	private static LayoutInflater inflater;
	
	public QuestDetailItemAdapter(Context context, List<QuestDetailItem> items) {
		super();
		this.context = context.getApplicationContext();
		this.items = items;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		TextView descriptionTextView;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.quest_item, null);
			holder = new ItemViewHolder();
			holder.nameTextView = (TextView) convertView.findViewById(R.id.quest_item_xml_textview_name);
			holder.descriptionTextView = (TextView) convertView.findViewById(R.id.quest_item_xml_textview_description);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}
		
		holder.nameTextView.setText(items.get(position).getName());
		holder.descriptionTextView.setText(items.get(position).getDescription());
		return convertView;
	}
}
