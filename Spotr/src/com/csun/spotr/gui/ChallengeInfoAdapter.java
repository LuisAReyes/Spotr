/**
 * @author Aleksandr Rozenman
 * @author Adam Brakel
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
import android.widget.RatingBar;
import android.widget.ProgressBar;

import com.csun.spotr.R;
import com.csun.spotr.core.CurrentChallenge;

public class ChallengeInfoAdapter extends BaseAdapter {
	private Activity context;
	
	public ChallengeInfoAdapter(Activity activityContext) {
		context = activityContext;
	}
	
	public int getCount() {
		return 1;
	}
	
	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}
	
	private static class InfoViewHolder {
		TextView nameTextView;
		TextView descriptionTextView;
		CheckBox flagCheckBox;
		RatingBar avgratingRatingBar;
		ProgressBar progressProgressBar;
		TextView locationTextView;
		TextView pointsTextView;
		TextView avgratingTextView;
		TextView attemptedTextView;
		TextView completedTextView;
		TextView categoryTextView;
	}
	
	/**
	 * TODO: Make this stuff actually display. Unsure how to do it. Needs research.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		InfoViewHolder holder;
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.challenge_info, null);
			holder = new InfoViewHolder();
			holder.nameTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_name);
			holder.flagCheckBox = (CheckBox)convertView.findViewById(R.id.challenge_info_xml_checkbox_reviewflag);
			holder.descriptionTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_description);
			holder.avgratingRatingBar = (RatingBar)convertView.findViewById(R.id.challenge_info_xml_ratingbar_rating);
			holder.progressProgressBar = (ProgressBar)convertView.findViewById(R.id.challenge_info_xml_progressbar_progress);
			holder.locationTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_location);
			holder.pointsTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_points);
			holder.avgratingTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_avgrating);
			holder.attemptedTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_attempted);
			holder.completedTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_completed);
			holder.categoryTextView = (TextView)convertView.findViewById(R.id.challenge_info_xml_textview_category);
			convertView.setTag(holder);
		}
		else {
			holder = (InfoViewHolder) convertView.getTag();
		}
		
		holder.nameTextView.setText(CurrentChallenge.name);
		holder.descriptionTextView.setText(CurrentChallenge.description);
		holder.flagCheckBox.setChecked(CurrentChallenge.reviewFlag);
		holder.avgratingRatingBar.setRating(CurrentChallenge.averageRating);
		if(-1 == CurrentChallenge.progress)
			holder.progressProgressBar.setProgress(0);
		else
			holder.progressProgressBar.setProgress(CurrentChallenge.progress);
		holder.locationTextView.setText(CurrentChallenge.location);
		holder.pointsTextView.setText(CurrentChallenge.points);
		holder.avgratingTextView.setText(Float.toString(CurrentChallenge.averageRating));
		holder.attemptedTextView.setText(CurrentChallenge.numStarted);
		holder.completedTextView.setText(CurrentChallenge.numCompleted);
		String category = "Categories: " + CurrentChallenge.category[0];
		for(int i = 1; i < CurrentChallenge.category.length; i++)
			category += ", " + CurrentChallenge.category[i];
		holder.categoryTextView.setText(category);
		
		convertView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View convertView) {
				
			}
		});
		
		return convertView;
	}
}
