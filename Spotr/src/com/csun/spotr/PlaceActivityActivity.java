package com.csun.spotr;

import com.csun.spotr.core.Challenge;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class PlaceActivityActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_activity);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.place_activity_xml_linearlayout_main);
        for (int i = 0; i < 10; ++i) {
        	addTable(mainLayout);
        }
    }
    
    public void addTable(LinearLayout layout) {
	    TableLayout table = new TableLayout(this);
	    addTitleRow(table, "channguyen", "@csun", "5 months ago", BitmapFactory.decodeResource(this.getResources(), R.drawable.monkey));
	    addCheckInRow(table);
	    addSeparatorRow(table);
	    addSnapPictureRow(table, BitmapFactory.decodeResource(this.getResources(), R.drawable.elephant));
	    addSeparatorRow(table);
	    addWriteWallRow(table, "Hello Android");
	    addSeparatorRow(table);
	    layout.addView(table);
	    addSeperatorLine(layout);
    }
    
    private void addSeperatorLine(LinearLayout layout) {
    	View line = new View(this);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 2);
    	params.setMargins(0, 2, 0, 2);
		line.setBackgroundColor(Color.YELLOW);
		layout.addView(line, params);
    }
    
    private void addSnapPictureRow(TableLayout table, Bitmap bitmap) {
    	TableRow row = new TableRow(this);	
    	LinearLayout outerLayout = new LinearLayout(this);
    	// add text
    	TextView text = new TextView(this);
    	text.setText("Snap Picture");
    	text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    	LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	textLayoutParams.setMargins(100, 0, 0, 0);
    	// add picture
    	ImageView picture = new ImageView(this);
    	LinearLayout.LayoutParams pictureLayoutParams = new LinearLayout.LayoutParams(150, 150);
    	picture.setImageBitmap(bitmap);
    	pictureLayoutParams.setMargins(80, 0, 0, 0);
    	// add layout
    	outerLayout.addView(text, textLayoutParams);
    	outerLayout.addView(picture, pictureLayoutParams);
    	row.addView(outerLayout);
    	// add row to table
    	table.addView(row);
    }
    
    private void addCheckInRow(TableLayout table) {
    	TableRow row = new TableRow(this);	
    	// create a TextView
    	TextView text = new TextView(this);
    	text.setText("Check In");
    	text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    	TableRow.LayoutParams textLayoutParams = new TableRow.LayoutParams();
    	textLayoutParams.setMargins(100, 0, 0, 0);
    	// add this TextView to row
    	row.addView(text, textLayoutParams);
    	// add row to table
    	table.addView(row);
    }
    
    void addSeparatorRow(TableLayout table) {
    	TableRow separator = new TableRow(this);
    	View line = new View(this);
    	TableRow.LayoutParams separatorLayoutParams = new TableRow.LayoutParams(300, 1);
    	separatorLayoutParams.setMargins(100, 0, 0, 0);
		line.setBackgroundColor(Color.BLUE);
		separator.addView(line, separatorLayoutParams);
    	table.addView(separator);
    }
    
    private void addWriteWallRow(TableLayout table, String message) {
    	TableRow row = new TableRow(this);	
    	LinearLayout outerLayout = new LinearLayout(this);
    	outerLayout.setOrientation(LinearLayout.VERTICAL);
    	// add text
    	TextView text = new TextView(this);
    	text.setText("Write On Wall");
    	text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    	
    	// add content 
    	TextView content = new TextView(this);
    	content.setText(message);
    	
    	// create params
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	params.setMargins(100, 0, 0, 0);
 
    	outerLayout.addView(text, params);
    	outerLayout.addView(content, params);
    	row.addView(outerLayout);
    	// add row to table
    	table.addView(row);
    }
    
    private void addTitleRow(TableLayout table, String username, String place, String time, Bitmap bitmap) {
    	TableRow row = new TableRow(this);	
    	LinearLayout outerLayout = new LinearLayout(this);
    	outerLayout.setOrientation(LinearLayout.HORIZONTAL);
    	// add picture
    	ImageView picture = new ImageView(this);
    	LinearLayout.LayoutParams layoutParamsPicture = new LinearLayout.LayoutParams(80, 80);
    	picture.setImageBitmap(bitmap);
    	
    	LinearLayout innerLayout = new LinearLayout(this);
    	innerLayout.setOrientation(LinearLayout.VERTICAL);
    	LinearLayout.LayoutParams layoutParamsInnerLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	layoutParamsInnerLayout.setMargins(20, 0, 0, 0);
    	
    	TextView textViewUsername = new TextView(this);
    	textViewUsername.setText(username + " : " + time);
    	textViewUsername.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    	LinearLayout.LayoutParams layoutParamsUsername = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	
     	// add content 
    	TextView textViewPlace = new TextView(this);
    	textViewPlace.setText(place);
    	LinearLayout.LayoutParams layoutParamsPlace = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    	
    	innerLayout.addView(textViewUsername, layoutParamsUsername);
    	innerLayout.addView(textViewPlace, layoutParamsPlace);
    	
    	outerLayout.addView(picture, layoutParamsPicture);
    	outerLayout.addView(innerLayout, layoutParamsInnerLayout);
    	row.addView(outerLayout);
    	// add row to table
    	table.addView(row);
    }
}
