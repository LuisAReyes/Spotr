package com.csun.spotr.adapter;

import java.util.List;

import com.csun.spotr.core.Weapon;
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

public class WeaponAdapter extends BaseAdapter {
	private List<Weapon> items;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private ItemViewHolder holder;

	public WeaponAdapter(Context context, List<Weapon> items) {
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
		TextView textViewTitle;
		TextView textViewPower;
		TextView textViewTimesLeft;
		ImageView imageViewIcon;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.weapon_item, null);
			holder = new ItemViewHolder();
			holder.textViewTitle = (TextView) convertView.findViewById(R.id.weapon_item_xml_textview_title);
			holder.textViewTimesLeft = (TextView) convertView.findViewById(R.id.weapon_item_xml_textview_times_left);
			holder.textViewPower = (TextView) convertView.findViewById(R.id.weapon_item_xml_textview_power);
			holder.imageViewIcon = (ImageView) convertView.findViewById(R.id.weapon_item_xml_imageview_icon);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.textViewTitle.setText(items.get(position).getTitle());
		holder.textViewPower.setText(Double.toString(items.get(position).getPointPercentage()));
		holder.textViewTimesLeft.setText(Integer.toString(items.get(position).getTimesLeft()));
		imageLoader.displayImage(items.get(position).getIconUrl(), holder.imageViewIcon);
		return convertView;
	}
}