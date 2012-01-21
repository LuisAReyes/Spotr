package com.csun.spotr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	private final String TAG = "[SettingActivity]";
	private String[] defaultView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		defaultView = getResources().getStringArray(R.array.setting_xml_spinner_defaultview_ary);
		Spinner s1 = (Spinner) findViewById(R.id.setting_xml_spinner_defaultlocation);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, defaultView);

		s1.setAdapter(adapter);
		s1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int index = arg0.getSelectedItemPosition();
				Toast.makeText(getBaseContext(), "You have selected item: " + defaultView[index], Toast.LENGTH_SHORT).show();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
	}
}
