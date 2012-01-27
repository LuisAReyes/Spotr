package com.csun.spotr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CreateLostItemActivity extends Activity {
	private static final String TAG = "(CreateLostItemActivity)";
	private EditText editTextName;
	private EditText editTextDescription;
	private EditText editTextPoints;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_lost_item);
		
		// hide keyboard right away
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		editTextName = (EditText) findViewById(R.id.create_lost_item_xml_edittext_name);
		editTextDescription = (EditText) findViewById(R.id.create_lost_item_xml_edittext_description);
		editTextPoints = (EditText) findViewById(R.id.create_lost_item_xml_edittext_points);
		
		imm.hideSoftInputFromWindow(editTextName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editTextDescription.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editTextPoints.getWindowToken(), 0);
	}
}
