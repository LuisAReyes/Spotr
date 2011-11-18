package com.csun.spotr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static final String TAG = "LoginActivity";
	
	CheckBox checkVisible;
	CheckBox checkSavePassword;
	EditText edittextPassword;
	EditText edittextUsername;
	Button buttonLogin;
	Button buttonSignup;
	
	// Zach Duvall (11/15/2011)
	SharedPreferences prefs;
	SharedPreferences.Editor editor;
	boolean prefsSavePassword;
	String prefsUsername;
	String prefsPassword;
	
	boolean passwordVisible = false;
	boolean savePassword = false;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        // Zach Duvall (11/15/2011)
        prefs = getSharedPreferences("Spotr", MODE_PRIVATE);
        prefsSavePassword = prefs.getBoolean("savePassword", false);
        prefsUsername = prefs.getString("username", "");
        prefsPassword = prefs.getString("password", "");
        
        // Tu Tran was here 11/9/2011
        checkVisible = (CheckBox)findViewById(R.id.login_xml_checkbox_visible_characters);
        checkSavePassword = (CheckBox)findViewById(R.id.login_xml_checkbox_remember_password);
        edittextPassword = (EditText)findViewById(R.id.login_xml_edittext_password_id);
        edittextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edittextUsername = (EditText)findViewById(R.id.login_xml_edittext_email_id);
        buttonLogin = (Button)findViewById(R.id.login_xml_button_login);
        buttonSignup = (Button)findViewById(R.id.login_xml_button_signup);
        
        // Zach Duvall (11/15/2011)
        if(prefsSavePassword) {
        	edittextUsername.append(prefsUsername);
        	edittextPassword.append(prefsPassword);
        }
        
        checkVisible.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				if(passwordVisible){
					edittextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					passwordVisible = false;
				}
				else {
					edittextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					passwordVisible = true;
				}
			}
        	
        });
        	
        buttonLogin.setOnClickListener(new OnClickListener() {
        	InputStream is;
        	String url = "http://107.22.209.62/android/login.php";
        	
			
			public void onClick(View v) {
				// get data from login Activity
				String username = edittextUsername.getText().toString();
				String password = edittextPassword.getText().toString();
				String result = "";
				
				
				//Add data into ValuePair format
				ArrayList<NameValuePair> loginData = new ArrayList<NameValuePair>(2);
				loginData.add(new BasicNameValuePair("username", username));
				loginData.add(new BasicNameValuePair("password", password));
				
				try 
				{
					//Connect via HTTP -> Encode data -> Send data
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					httppost.setEntity(new UrlEncodedFormEntity(loginData));
					HttpResponse response = httpclient.execute(httppost);
					
					//get response data
					HttpEntity entity = response.getEntity();
					is = entity.getContent();
					Log.i("postdata", response.getStatusLine().toString());
					
				}
				catch (Exception e)
				{
					Log.e(TAG, "Error: " + e.toString());					
				}	
				
					//convert response to string
				try
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),10);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null)
					{
						sb.append(line + "\n");
					}
					is.close();
					result = sb.toString();
				}
				catch (Exception e)
				{
					Log.e(TAG, "Error: " + e.toString());					
				}
				
				//decode json data				
				try
				{					
					{						
						JSONArray jArray = new JSONArray(result);
						if (jArray.length() > 0)
						{							
							JSONObject jObject = jArray.getJSONObject(0);
							String userName = jObject.getString("username");
							
							// Zach Duvall (11/15/2011)
							if(savePassword) {
								editor = getSharedPreferences("Spotr", MODE_PRIVATE).edit();
								editor.putBoolean("savePassword", true);
								editor.putString("username", username);
								editor.putString("password", password);
								editor.commit();
							}
							
							startActivity(new Intent("com.csun.spotr.MainMenuActivity"));
						}
						
					}
					
				}
			
				catch (Exception e)
				{
					Log.e(TAG,"Error: " + e.toString());
					showDialog(0);					
				}
							
			}
			
        });
        buttonSignup.setOnClickListener(new OnClickListener() {
        	public void onClick (View v) {
        		startActivity(new Intent("com.csun.spotr.SignupActivity"));
        	}
        });
        
        // Zach Duvall (11/15/2011)
        checkSavePassword.setOnClickListener(new OnClickListener() {
        	public void onClick (View v) {
        		if(!savePassword)
        			savePassword = true;
        		else
        			savePassword = false;
        	}
        });
        
    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return new AlertDialog.Builder(this)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Error Message")
			.setMessage("Invalid Username/Password.\n Please try again.")
			.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton)
				{
					
				}
			})
			.create();
		}
		return null;
	}
    
    public void onPause() {
    	super.onPause();
    	finish();
    }
    
}