/**
 * @author Aleksandr Rozenman
 * @author Adam Brakel
 * @author: Chan Nguyen
 */

package com.csun.spotr.gui;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.csun.spotr.R;
import com.csun.spotr.core.CurrentChallenge;

import java.util.Random;

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
			holder.ratingTextView = (TextView) convertView.findViewById(R.id.challenge_item_xml_textview_challenge_rating);
			holder.flagCheckBox = (CheckBox) convertView.findViewById(R.id.challenge_item_xml_checkbox_flag);
			convertView.setTag(holder);
		}
		else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		holder.headerTextView.setText(header[position]);
		holder.bodyTextView.setText(body[position]);
		holder.ratingTextView.setText(rating[position]);
		holder.flagCheckBox.setChecked(flag[position]);
		
		convertView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View convertView) {
				Random rand = new Random();
				
				CurrentChallenge.name = ((ItemViewHolder)(convertView.getTag())).headerTextView.getText().toString();
				CurrentChallenge.averageRating = Float.parseFloat(((ItemViewHolder)(convertView.getTag())).ratingTextView.getText().toString());
				CurrentChallenge.reviewFlag = ((ItemViewHolder)(convertView.getTag())).flagCheckBox.isChecked();
				CurrentChallenge.numRatings = rand.nextInt(500000000) + 1;
				CurrentChallenge.points = rand.nextInt(100) + 1;
				CurrentChallenge.progress = (byte)(rand.nextInt(102) - 1);
				CurrentChallenge.description = ((ItemViewHolder)(convertView.getTag())).bodyTextView.getText().toString();
				CurrentChallenge.numStarted = rand.nextInt(CurrentChallenge.numRatings);
				CurrentChallenge.numCompleted = rand.nextInt(CurrentChallenge.numStarted);
				int numCategories = rand.nextInt(5) + 1;
				CurrentChallenge.category = new String[numCategories];
				for(int i = 0; i < numCategories; i++)
					CurrentChallenge.category[i] = "Cat" + i;
				CurrentChallenge.location = "Location goes here";
				
				Intent i = new Intent("com.csun.spotr.ChallengeInfoActivity");
				context.startActivity(i);
			}
		});
		return convertView;
	}
}