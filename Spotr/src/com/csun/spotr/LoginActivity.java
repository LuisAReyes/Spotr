package com.csun.spotr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.*;

public class LoginActivity extends Activity {
	Button userLogin;
	EditText passwordBox;
	EditText usernameBox;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		userLogin = (Button) findViewById(R.id.login_xml_button_login);
		passwordBox = (EditText) findViewById(R.id.login_xml_edittext_email_id);
		usernameBox = (EditText) findViewById(R.id.login_xml_edittext_email_id);
		userLogin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String username = usernameBox.getText().toString();
				Log.d("SpotrActivity", username);
				String password = passwordBox.getText().toString();
				Log.d("SpotrActivity", password);

				HttpClient client = new DefaultHttpClient();
				HttpRequest request = null;
				HttpResponse response = null;

				try {
					request = new HttpGet(new URI("http://107.22.209.62/android/login"));

				}
				catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				try {
					response = client.execute((HttpUriRequest) request);

				}
				catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
				}

				InputStream in = null;
				try {
					in = response.getEntity().getContent();
				}
				catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						str.append(line);
					}
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String html = new String("");
				html = str.toString();
				Log.d("Http", html);
			}
		});
	}
}
