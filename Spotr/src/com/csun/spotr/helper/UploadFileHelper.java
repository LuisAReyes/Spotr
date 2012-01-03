package com.csun.spotr.helper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.csun.spotr.singleton.CustomHttpClient;

import android.graphics.Bitmap;
import android.util.Log;

public class UploadFileHelper {
	private static final String TAG = "[UploadFileHelper]";
	private static final String URL = "http://107.22.209.62/images/upload_file_and_insert_to_database.php";
	
	public static boolean uploadFileTo(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		InputStream input = null;
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
		byte[] src = stream.toByteArray();
		String byteCode = Base64.encodeBytes(src);
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("image", byteCode));
		nameValuePairs.add(new BasicNameValuePair("file_name", "what.png"));
		try {
			HttpClient httpclient = CustomHttpClient.getHttpClient();
			HttpPost httppost = new HttpPost(URL);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			input = entity.getContent();
		}
		catch (Exception e) {
			Log.e("TAG", "Error in Http connection" + e.toString());
		}
		Log.w(":(", "what's wrong?");
		return true;
	}
	
	public static JSONObject uploadFileToServer(String url, List<NameValuePair> datas) {
		InputStream input = null;
		String result = "";
		JSONObject json = null;
		try {
			HttpClient httpclient = CustomHttpClient.getHttpClient();
			HttpPost httppost = new HttpPost(URL);
			httppost.setEntity(new UrlEncodedFormEntity(datas));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			input = entity.getContent();
		}
		catch (Exception e) {
			Log.e("TAG", "Error in Http connection" + e.toString());
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "iso-8859-1"), 8);
			StringBuilder content = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				content.append(line + "\n");
			}
			input.close();
			result = content.toString();
		}
		catch (Exception e) {
			Log.e(TAG + ".uploadFileToServer(String url, List<NameValuePair> datas)", "Error parsing result " + e.toString());
		}
		try {
			json = new JSONObject(result);
		}
		catch (JSONException e) {
			Log.e(TAG + ".uploadFileToServer(String url, List<NameValuePair> datas)", "Error converting data " + e.toString());
		}
		return json;
	}
}
