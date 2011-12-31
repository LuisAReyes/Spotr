package com.csun.spotr.gui;

import java.util.List;

import com.csun.spotr.R;
import com.csun.spotr.core.User;

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
	private List<User> userList;
	
	public LeaderboardItemAdapter(Activity context, List<User> userList) {
		super();
		this.context = context;
		this.userList = userList;
	}

	public int getCount() {
		return userList.size();
	}

	public Object getItem(int position) {
		return userList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	private static class ItemViewHolder {
		TextView textViewRank;
		TextView textViewUsername;
		TextView textViewChallengesDone;
		TextView textViewPlacesVisited;
		TextView textViewPoints;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.leaderboard_item, null);
			holder = new ItemViewHolder();
			holder.textViewRank = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_textview_rank);
			holder.textViewUsername = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_textview_username);
			holder.textViewChallengesDone = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_textview_cd);
			holder.textViewPlacesVisited = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_textview_pv);
			holder.textViewPoints = (TextView) convertView.findViewById(R.id.leaderboard_item_xml_textview_pts);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.textViewRank.setText(Integer.toString(userList.get(position).getRank()));
		holder.textViewUsername.setText(userList.get(position).getUsername());
		holder.textViewChallengesDone.setText(Integer.toString(userList.get(position).getChallengesDone()));
		holder.textViewPlacesVisited.setText(Integer.toString(userList.get(position).getPlacesVisited()));
		holder.textViewPoints.setText(Integer.toString(userList.get(position).getPoints()));
		return convertView;
	}
}