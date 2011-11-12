package com.csun.spotr.gui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.csun.spotr.R;

public class ProfileItemAdapter extends BaseAdapter {
	private Activity context;
	private String headers[];
	private String bodies[];

	public ProfileItemAdapter(Activity c, String[] h, String[] b) {
		super();

		context = c;
		headers = h;
		bodies = b;
	}

	public int getCount() {
		return headers.length;
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
		ViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.profile_item, null);
			holder = new ViewHolder();
			holder.textviewHeader = (TextView) convertView.findViewById(R.id.profile_item_xml_textview_profile_header);
			holder.textviewBody = (TextView) convertView.findViewById(R.id.profile_item_xml_textview_profile_body);

			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();

		holder.textviewHeader.setText(headers[position]);
		holder.textviewBody.setText(bodies[position]);

		return convertView;
	}
}