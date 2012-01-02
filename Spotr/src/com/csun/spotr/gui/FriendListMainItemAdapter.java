package com.csun.spotr.gui;

import java.util.List;

import com.csun.spotr.R;
import com.csun.spotr.core.User;
import com.csun.spotr.helper.DownloadImageHelper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListMainItemAdapter extends BaseAdapter {
	private Activity context;
	private List<User> users;

	public FriendListMainItemAdapter(Activity context, List<User> users) {
		super();
		this.context = context;
		this.users = users;
	}

	public int getCount() {
		return users.size();
	}

	public Object getItem(int position) {
		return users.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public static class ItemViewHolder {
		TextView textViewName;
		ImageView imageViewPicture;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.friend_list_main_item, null);
			holder = new ItemViewHolder();
			holder.textViewName = (TextView) convertView.findViewById(R.id.friend_list_main_item_xml_textview_name);
			holder.imageViewPicture = (ImageView) convertView.findViewById(R.id.friend_list_main_item_xml_imageview_picture);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.textViewName.setText(users.get(position).getUsername());
		try {
			holder.imageViewPicture.setImageDrawable(
				DownloadImageHelper.getImageFromUrl(users.get(position).getImageUrl()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	/*
	 * public android.widget.Filter getFilter() { return new
	 * android.widget.Filter() {
	 * 
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Override protected void publishResults(CharSequence constraint,
	 * FilterResults results) { }
	 * 
	 * @Override protected FilterResults performFiltering(CharSequence
	 * constraint) { FilterResults results = new FilterResults(); if (constraint
	 * != null && constraint.toString().length() > 0) {
	 * 
	 * } else { } return results; } }; }
	 */
}