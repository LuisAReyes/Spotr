package com.csun.spotr.adapter;

import java.util.List;

import com.csun.spotr.R;
import com.csun.spotr.core.adapter_item.QuestDetailItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuestDetailItemAdapter extends BaseAdapter{
	private Activity context;
	private List<QuestDetailItem> items;
	
	public QuestDetailItemAdapter(Activity context, List<QuestDetailItem> items) {
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
		TextView descriptionTextView;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
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
