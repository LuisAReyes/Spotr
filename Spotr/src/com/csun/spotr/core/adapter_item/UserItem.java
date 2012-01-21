package com.csun.spotr.core.adapter_item;

public class UserItem {
	private final int id;
	private final String username;
	private final String pictureUrl;

	public UserItem(int id, String username, String pictureUrl) {
		this.id = id;
		this.username = username;
		this.pictureUrl = pictureUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public int getId() {
		return id;
	}
}
