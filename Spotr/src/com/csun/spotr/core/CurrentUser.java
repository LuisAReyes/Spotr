package com.csun.spotr.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;

public class CurrentUser {
	private static final String	TAG = "[CurrentUser]";
	private static User	user;
	
	public static void setCurrentUser(int id, String username, String password) {
		user = new User.Builder(id, username, password).build();
	}
	
	public static User getCurrentUser() {
		return user;
	}
}
