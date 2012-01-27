package com.csun.spotr.core.adapter_item;

public class SeekingItem {
	private final int id;
	private final String imageUrl;
	private final String name;
	
	public SeekingItem(int id, String name, String imageUrl) {
		this.id = id;
		this.imageUrl = imageUrl;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public String getImageUrl() {
		return imageUrl;
	}
	
	public String getName() {
		return name;
	}
}
