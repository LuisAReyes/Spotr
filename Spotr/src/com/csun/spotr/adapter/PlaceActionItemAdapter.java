package com.csun.spotr.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.csun.spotr.R;
import com.csun.spotr.core.Challenge;

/**
 * Adapter for each challenge item in the Missions tab of Spots. 
 */
public class PlaceActionItemAdapter extends BaseAdapter {
	private List<Challenge> items;
	private Context context;
	private static LayoutInflater inflater;
	private ItemViewHolder holder;

	public PlaceActionItemAdapter(Context context, List<Challenge> items) {
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

	public static class ItemViewHolder {
		TextView titleTextView;
		TextView descriptionTextView;
		TextView pointTextView;
		TextView pointAbbrevTextView;
		ImageView iconImageView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.place_action_item, null);
			holder = new ItemViewHolder();
			holder.titleTextView = (TextView) convertView.findViewById(R.id.place_action_item_xml_textview_title);
			holder.pointTextView = (TextView) convertView.findViewById(R.id.place_action_item_xml_textview_point);
			holder.pointAbbrevTextView = (TextView) convertView.findViewById(R.id.place_action_item_xml_textview_point_abbrev);
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.place_action_item_xml_imageview_action_icon);

			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.titleTextView.setText(items.get(position).getName());
		holder.pointTextView.setText(Integer.toString(items.get(position).getPoints()) + " "); // extra space at end to avoid clipping off italic text
		holder.pointAbbrevTextView.setText(R.string.place_action_item_xml_string_pts);

		if (items.get(position).getType() == Challenge.Type.CHECK_IN)
			holder.iconImageView.setImageResource(R.drawable.ic_launcher);
		else if (items.get(position).getType() == Challenge.Type.SNAP_PICTURE)
			holder.iconImageView.setImageResource(R.drawable.ic_launcher);
		else if (items.get(position).getType() == Challenge.Type.WRITE_ON_WALL)
			holder.iconImageView.setImageResource(R.drawable.ic_launcher);
		else if (items.get(position).getType() == Challenge.Type.WRITE_ON_WALL)
			holder.iconImageView.setImageResource(R.drawable.ic_launcher);
		else
			holder.iconImageView.setImageResource(R.drawable.ic_launcher);
		
		return convertView;
	}
}