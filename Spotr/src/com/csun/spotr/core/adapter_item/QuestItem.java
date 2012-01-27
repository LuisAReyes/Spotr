package com.csun.spotr.core.adapter_item;

public class QuestItem {
	private final int id;
	private final String name;
	private final String description;
	private final int points;
	private final int spotnum;
	private final String url;

	public QuestItem(int id, String name, int points, int spotnum, String description, String url) {
		this.id = id;
		this.name = name;
		this.points = points;
		this.spotnum = spotnum;
		this.description = description;
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getPoints() {
		return points;
	}
	
	public int getSpotnum() {
		return spotnum;
	}
	public String getDescription() {
		return description;
	}
	
	public String getUrl() {
		return url;
	}
}
