package com.csun.spotr.singleton;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.csun.spotr.core.User;

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