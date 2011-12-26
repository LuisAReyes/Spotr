package com.csun.spotr.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.graphics.Bitmap;
import android.os.Build;

public class User {
	private final int id;
	private final String username;
	private final Date dateJoin;
	private String password;

	private String realname;
	private Date dateOfBirth;
	private int points;
	private Bitmap picture;
	private List<Integer> friendIds = null;
	private List<Integer> badgeIds = null;
	private List<Integer> challengeIds = null;
	private List<Integer> placesVisitedIds = null;

	public static class Builder {
		private final int id;
		private final String username;
		private final Date dateJoin;
		private String password;

		private String realname;
		private Date dateOfBirth;
		private int points;
		private Bitmap picture;
		private List<Integer> friendIds;
		private List<Integer> badgeIds;
		private List<Integer> challengeIds;
		private List<Integer> placesVisitedIds;

		public Builder(int id, String username, String password, Date dateJoin) {
			// required parameters
			this.id = id;
			this.username = username;
			this.dateJoin = dateJoin;
			this.password = password;

			// optional parameters
			realname = username;
			dateOfBirth = null;
			points = 0;
			picture = null;
			friendIds = new ArrayList<Integer>();
			badgeIds = new ArrayList<Integer>();
			challengeIds = new ArrayList<Integer>();
			placesVisitedIds = new ArrayList<Integer>();
		}

		public Builder realname(String realname) {
			this.realname = realname;
			return this;
		}

		public Builder dateOfBirth(Date dateOfBirDate) {
			this.dateOfBirth = dateOfBirDate;
			return this;
		}

		public Builder points(int points) {
			this.points = points;
			return this;
		}

		public Builder picture(Bitmap picture) {
			this.picture = picture;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}

	public User(Builder builder) {
		id = builder.id;
		username = builder.username;
		dateJoin = builder.dateJoin;

	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

	public List<Integer> getFriendIds() {
		return new ArrayList<Integer>(friendIds);
	}

	public void addFriendId(Integer id) {
		this.friendIds.add(id);
	}

	public boolean removeFriendId(Integer id) {
		if (this.friendIds.remove(id)) {
			return true;
		}
		return false;
	}

	public List<Integer> getBadgeIds() {
		return new ArrayList<Integer>(badgeIds);
	}

	public void addBadgeId(Integer id) {
		this.badgeIds.add(id);
	}

	public boolean removeBadgeId(Integer id) {
		if (this.badgeIds.remove(id)) {
			return true;
		}
		return false;
	}

	public List<Integer> getChallengeIds() {
		return new ArrayList<Integer>(challengeIds);
	}

	public void addChallengeId(Integer id) {
		this.challengeIds.add(id);
	}

	public boolean removeChallenge(Integer id) {
		if (this.challengeIds.remove(id)) {
			return true;
		}
		return false;
	}

	public List<Integer> getPlacesVisitedIds() {
		return new ArrayList<Integer>(placesVisitedIds);
	}

	public void setPlacesVisitedIds(Vector<Integer> placesVisitedIds) {
		this.placesVisitedIds = placesVisitedIds;
	}

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public Date getDateJoin() {
		return dateJoin;
	}
}
