package com.csun.spotr.gui;

import com.csun.spotr.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class LeaderboardItemAdapter extends BaseAdapter {
	private Activity context;
	private String username[];
	private String status[];
	private String rank[];
	private int imageId[];

	public LeaderboardItemAdapter(Activity activityContext, String[] username, String[] status, String[] rank, int[] imageId) {
		super();
		this.context = activityContext;
		this.username = username;
		this.status = status;
		this.rank = rank;
		this.imageId = imageId;
	}

	public int getCount() {
		return username.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public static class ItemViewHolder {
		TextView usernameTextView;
		TextView statusTextView;
		TextView rankTextView;
		ImageView imageIdImageView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.leaderboard_item, null);
			holder = new ItemViewHolder();
			holder.usernameTextView = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_leaderboard_item_user_name);
			holder.statusTextView = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_leaderboard_item_user_status);
			holder.rankTextView = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_leaderboard_item_user_rank);
			holder.imageIdImageView = (ImageView) convertView.findViewById(R.id.leaderboard_item_xml_leaderboard_item_user_image);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.usernameTextView.setText(username[position]);
		holder.statusTextView.setText(status[position]);
		holder.rankTextView.setText(rank[position]);
		holder.imageIdImageView.setImageResource(imageId[position]);
		return convertView;
	}
}