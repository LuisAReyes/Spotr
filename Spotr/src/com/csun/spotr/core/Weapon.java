package com.csun.spotr.core;

public class Weapon {
	private final int id;
	private String title;
	private String iconUrl;
	private int timesLeft;
	private double pointPercentage;
	
	public Weapon(int id, String title, String iconUrl, double pointPercentage, int timesLeft) {
		this.id = id;
		this.title = title;
		this.iconUrl = iconUrl;
		this.pointPercentage = pointPercentage;
		this.timesLeft = timesLeft;
	}

	public int getTimesLeft() {
		return timesLeft;
	}

	public void setTimesLeft(int timesLeft) {
		this.timesLeft = timesLeft;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public double getPointPercentage() {
		return pointPercentage;
	}

	public void setPointPercentage(double pointPercentage) {
		this.pointPercentage = pointPercentage;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Weapon other = (Weapon) obj;
		if (id != other.id)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		}
		else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Weapon [id=" + id + ", title=" + title + ", iconUrl=" + iconUrl + ", pointPercentage=" + pointPercentage + "]";
	}
}
