package com.csun.spotr.gui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.csun.spotr.R;

public class ChallengeItemAdapter extends BaseAdapter {
	private Activity context;
	private String header[];
	private String body[];
	private String rating[];
	boolean[] flag;

	public ChallengeItemAdapter(Activity activityContext, String[] header, String[] body, String[] rating, boolean[] flag) {
		super();
		this.context = activityContext;
		this.header = header;
		this.body = body;
		this.rating = rating;
		this.flag = flag;
	}

	public int getCount() {
		return header.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	private static class ItemViewHolder {
		TextView headerTextView;
		TextView bodyTextView;
		CheckBox flagCheckBox;
		TextView ratingTextView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.challenge_item, null);
			holder = new ItemViewHolder();
			holder.headerTextView = (TextView) convertView.findViewById(R.id.challenge_item_xml_textview_challenge_item_header);
			holder.bodyTextView = (TextView) convertView.findViewById(R.id.challenge_item_xml_textview_challenge_item_body);
//			holder.ratingTextView = (TextView) convertView.findViewById(R.id.challenge_item_xml_textview_challenge_rating);
//			holder.flagCheckBox = (CheckBox) convertView.findViewById(R.id.challenge_item_xml_checkbox_flag);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.headerTextView.setText(header[position]);
		holder.bodyTextView.setText(body[position]);
		holder.bodyTextView.setText(rating[position]);
		return convertView;
	}
}