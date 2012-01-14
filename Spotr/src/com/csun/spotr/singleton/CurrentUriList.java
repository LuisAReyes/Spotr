package com.csun.spotr.singleton;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

public class CurrentUriList {
	private static List<Uri> uriList = new ArrayList<Uri>();
	
	public static void addUri(Uri uri) {
		uriList.add(uri);
	}
	
	public static List<Uri> getUriList() {
		return uriList;
	}
}
