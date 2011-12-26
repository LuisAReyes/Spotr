package com.csun.spotr.core;

public class PlaceActionItem {
	private String title;
	private String description;
	private String points;
	private int    icon;

	public PlaceActionItem(String title, String description, String points, int icon) {
		this.title = title;
		this.description = description;
		this.points = points;
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
}
