package com.csun.spotr;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends Activity {
	private static final String TAG = "(WebviewActivity)";
	private String webUrl = "http://google.com/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		// get place id
		Bundle extrasBundle = getIntent().getExtras();
		webUrl = extrasBundle.getString("place_web_url");
		WebView wv = (WebView) findViewById(R.id.webview_xml_place_webview);
		wv.setWebViewClient(new WebCallBack());
		WebSettings settings = wv.getSettings();
		settings.setBuiltInZoomControls(true);
		wv.loadUrl(webUrl);
	}

	private class WebCallBack extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return false;
		}
	}
}
