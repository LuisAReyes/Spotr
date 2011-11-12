package com.csun.spotr.core;

/**
 * @author Chan Nguyen
 */
public class PlaceInfo {
	private String name;
	private String vicinity;
	private String category;
	private String coordinate;
	
	public PlaceInfo(String name, String vicinity, String category, String coordinate) {
		this.name = name;
		this.vicinity = vicinity;
		this.category = category;
		this.coordinate = coordinate;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getVicinity() {
		return vicinity;
	}
	
	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCoordinate() {
		return coordinate;
	}
	
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
	}
}
