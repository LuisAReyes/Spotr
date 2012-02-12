package com.csun.spotr.adapter;

import java.util.List;

import com.csun.spotr.core.adapter_item.UserItem;
import com.csun.spotr.R;
import com.csun.spotr.util.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserItemAdapter extends BaseAdapter {
	private Context context;
	private List<UserItem> items;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private ItemViewHolder viewHolder;

	public UserItemAdapter(Context context, List<UserItem> items) {
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
		TextView textViewName;
		ImageView imageViewPicture;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.friend_list_main_item, null);
			viewHolder = new ItemViewHolder();
			viewHolder.textViewName = (TextView) convertView.findViewById(R.id.friend_list_main_item_xml_textview_name);
			viewHolder.imageViewPicture = (ImageView) convertView.findViewById(R.id.friend_list_main_item_xml_imageview_picture);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ItemViewHolder) convertView.getTag();
		}

		viewHolder.textViewName.setText(items.get(position).getUsername());
		imageLoader.displayImage(items.get(position).getPictureUrl(), viewHolder.imageViewPicture);
		return convertView;
	}
}