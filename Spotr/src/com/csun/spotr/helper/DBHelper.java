package com.csun.spotr.helper;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

/**
 * @author: Vlad Kopman
 * 
 * This file connects to service.php on the webserver to push and retrieve data from the database
 * 
 * TODO
 * - HTTP post and responses need to be refactored out of each method
 */

public class DBHelper {
//	private final String "TAG" = "DBHelper";
	private int user_id;
	private String username;
	private static String url = "http://107.22.209.62/android/service.php";
	InputStream is;
	String result = "";
	
	
	// LOGIN METHOD
	public boolean login( ArrayList<NameValuePair> loginData ){
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
			Log.e("log_tag", "Error: " + e.toString());					
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
			Log.e("log_tag", "Error: " + e.toString());					
		}
		
		//decode json data				
		try
		{					
			{						
				JSONArray jArray = new JSONArray(result);
				if (jArray.length() > 0)
				{							
					JSONObject jObject = jArray.getJSONObject(0);
					username = jObject.getString("username");
					user_id = Integer.parseInt(jObject.getString("id"));	
					return true;
				}
			}
		}
		catch (Exception e)
		{
			Log.e("log_tag","Error: " + e.toString());
		//	showDialog(0);					
		}
		return false;	
			
	}
		
	// GET FRIENDS METHOD	
	public void getFriends(){
		ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>(2);
		postData.add(new BasicNameValuePair("type", "getFriends"));
		postData.add(new BasicNameValuePair("id", Integer.toString(user_id)));
		try 
		{
			//Connect via HTTP -> Encode data -> Send data
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(postData));
			HttpResponse response = httpclient.execute(httppost);
			
			//get response data
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			Log.i("postdata", response.getStatusLine().toString());
		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error: " + e.toString());					
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
			Log.e("log_tag", "Error: " + e.toString());					
		}
		
		//decode json data				
		try
		{					
			{						
				JSONArray jArray = new JSONArray(result);
				if (jArray.length() > 0)
				{							
					// JSON DATA WILL BE PROCESSED FROM HERE					
					JSONObject jObject = jArray.getJSONObject(0);
					username = jObject.getString("friends_id");
					Log.d("Tag", username);
				//	id = jObject.getString("id");	
				}
			}
		}
	
		catch (Exception e)
		{
			Log.e("log_tag","Error: " + e.toString());
		//	showDialog(0);					
		}		
	}

	// GET FRIENDS METHOD	
	public void getSpots (long latitude, long longitude){
		ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>(3);
		postData.add(new BasicNameValuePair("type", "getSpots"));
		postData.add(new BasicNameValuePair("lan", Long.toString(latitude)));
		postData.add(new BasicNameValuePair("lon", Long.toString(longitude)));
		try 
		{
			//Connect via HTTP -> Encode data -> Send data
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(postData));
			HttpResponse response = httpclient.execute(httppost);
			
			//get response data
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			Log.i("postdata", response.getStatusLine().toString());
		}
		catch (Exception e)
		{
			Log.e("log_tag", "Error: " + e.toString());					
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
			Log.e("log_tag", "Error: " + e.toString());					
		}
		
		//decode json data				
		try
		{					
			{						
				JSONArray jArray = new JSONArray(result);
				if (jArray.length() > 0)
				{			
					// JSON DATA WILL BE PROCESSED FROM HERE
					JSONObject jObject = jArray.getJSONObject(0);
					username = jObject.getString("friends_id");
				//	Log.d("Tag", username);
				//	id = jObject.getString("id");	
				}
			}
		}
	
		catch (Exception e)
		{
			Log.e("log_tag","Error: " + e.toString());
		//	showDialog(0);					
		}		
	}
	// gets locations around the user

	public void getChallenges(){}
	// returns challenges for the spot, or all local challenges if spot_id is null

	public void setMessage(){}
	// adds a message to a spot, can be extended later to work messaging, walls, etc

	public void setAddFriend(){}
	// adds a friend from the phone, friends can be removed similarly


}
