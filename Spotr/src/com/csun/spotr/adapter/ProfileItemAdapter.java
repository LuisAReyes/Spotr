package com.csun.spotr.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csun.spotr.R;

public class ProfileItemAdapter extends BaseAdapter {
	private Context context;
	private List<String> headers;
	private List<String> bodies;
	private static LayoutInflater inflater;
	private	ViewHolder holder;

	public ProfileItemAdapter(Context context, List<String> headers, List<String> bodies) {
		super();
		this.context = context.getApplicationContext();
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.headers = headers;
		this.bodies = bodies;
	}

	public int getCount() {
		return headers.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	private static class ViewHolder {
		TextView textviewHeader;
		TextView textviewBody;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.profile_item, null);
			holder = new ViewHolder();
			holder.textviewHeader = (TextView) convertView.findViewById(R.id.profile_item_xml_textview_header);
			holder.textviewBody = (TextView) convertView.findViewById(R.id.profile_item_xml_textview_body);

			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textviewHeader.setText(headers.get(position));
		holder.textviewBody.setText(bodies.get(position));
		return convertView;
	}
}