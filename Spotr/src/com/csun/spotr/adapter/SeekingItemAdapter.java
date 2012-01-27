package com.csun.spotr.adapter;

import java.util.List;

import com.csun.spotr.R;
import com.csun.spotr.core.adapter_item.SeekingItem;
import com.csun.spotr.util.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class SeekingItemAdapter extends BaseAdapter {
	private Context context;
	private List<SeekingItem> items;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private ItemViewHolder viewHolder;
	
	public SeekingItemAdapter(Context c, List<SeekingItem> items) {
		this.context = c;
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
		ImageView imageViewPicture;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.seeking_item, null);
			viewHolder = new ItemViewHolder();
			viewHolder.imageViewPicture = (ImageView) convertView.findViewById(R.id.seeking_item_xml_imageview_item_picture);
			viewHolder.imageViewPicture.setLayoutParams(new GridView.LayoutParams(85, 85));
            viewHolder.imageViewPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.imageViewPicture.setPadding(8, 8, 8, 8);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ItemViewHolder) convertView.getTag();
		}

		imageLoader.displayImage(items.get(position).getImageUrl(), viewHolder.imageViewPicture);
		return convertView;
	}
}
