/**
 * @author Aleksandr Rozenman
 * @author Adam Brakel
 */

package com.csun.spotr;

import com.csun.spotr.core.CurrentChallenge;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ChallengeInfoActivity extends Activity {
	private final String TAG = "[ChallengeInfoActivity]";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.challenge_info);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		/*challengeInfoListView = new ListView(this);
		challengeInfo = new ChallengeInfoAdapter(this);
		challengeInfoListView.setAdapter(challengeInfo);
		challengeInfoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});*/
		
		TextView nameTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_name);
		CheckBox flagCheckBox = (CheckBox)findViewById(R.id.challenge_info_xml_checkbox_reviewflag);
		TextView descriptionTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_description);
		RatingBar avgratingRatingBar = (RatingBar)findViewById(R.id.challenge_info_xml_ratingbar_rating);
		ProgressBar progressProgressBar = (ProgressBar)findViewById(R.id.challenge_info_xml_progressbar_progress);
		TextView locationTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_location);
		TextView pointsTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_points);
		TextView avgratingTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_avgrating);
		TextView attemptedTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_attempted);
		TextView completedTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_completed);
		TextView categoryTextView = (TextView)findViewById(R.id.challenge_info_xml_textview_category);
		
		
		nameTextView.setText(CurrentChallenge.name);
		descriptionTextView.setText(CurrentChallenge.description);
		flagCheckBox.setChecked(CurrentChallenge.reviewFlag);
		avgratingRatingBar.setRating(CurrentChallenge.averageRating);
		
		if(-1 == CurrentChallenge.progress)
			progressProgressBar.setProgress(0);
		else
			progressProgressBar.setProgress(CurrentChallenge.progress);
		
		locationTextView.setText(CurrentChallenge.location);
		pointsTextView.setText(Integer.toString(CurrentChallenge.points));
		avgratingTextView.setText(Float.toString(CurrentChallenge.averageRating));
		attemptedTextView.setText(Integer.toString(CurrentChallenge.numStarted));
		completedTextView.setText(Integer.toString(CurrentChallenge.numCompleted));
		String category = "Categories: " + CurrentChallenge.category[0];
		
		for(int i = 1; i < CurrentChallenge.category.length; i++)
			category += ", " + CurrentChallenge.category[i];
		
		categoryTextView.setText(category);
	}
}
