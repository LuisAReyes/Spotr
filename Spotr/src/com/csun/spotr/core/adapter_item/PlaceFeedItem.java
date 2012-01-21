package com.csun.spotr.core.adapter_item;

import com.csun.spotr.core.Challenge;
import com.csun.spotr.core.Challenge.Type;

import android.net.Uri;

public class PlaceFeedItem {
	// required parameters
	private final int id;
	private final String username;
	private final Challenge.Type challengeType;
	private final String time;

	// optional parameters
	private String name;
	private String description;
	private Uri snapPictureUri = null;
	private Uri userPictureUri = null;
	private String comment;

	public static class Builder {
		// required parameters
		private final int id;
		private final String username;
		private final Challenge.Type challengeType;
		private final String time;

		// optional parameters
		private String name = "";
		private String description = "";
		private Uri snapPictureUri = null;
		private Uri userPictureUri = null;
		private String comment = "";

		public Builder(int id, String username, Challenge.Type challengeType, String time) {
			this.id = id;
			this.username = username;
			this.challengeType = challengeType;
			this.time = time;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder description(String description) {
			this.description = description;
			return this;
		}

		public Builder userPictureUri(Uri uri) {
			this.userPictureUri = uri;
			return this;
		}

		public Builder snapPictureUri(Uri uri) {
			this.snapPictureUri = uri;
			return this;
		}

		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}

		public PlaceFeedItem build() {
			return new PlaceFeedItem(this);
		}
	}

	public PlaceFeedItem(Builder builder) {
		this.id = builder.id;
		this.username = builder.username;
		this.challengeType = builder.challengeType;
		this.time = builder.time;
		this.name = builder.name;
		this.description = builder.description;
		this.userPictureUri = builder.userPictureUri;
		this.snapPictureUri = builder.snapPictureUri;
		this.comment = builder.comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Uri getSnapPictureUri() {
		return snapPictureUri;
	}

	public void setSnapPictureUri(Uri uri) {
		this.snapPictureUri = uri;
	}

	public Uri getUserPictureUri() {
		return userPictureUri;
	}

	public void setUserPictureUri(Uri uri) {
		this.userPictureUri = uri;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public Challenge.Type getChallengeType() {
		return challengeType;
	}

	public String getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "PlaceLog [id=" + id + ", username=" + username + ", challengeType=" + challengeType + ", time=" + time + ", name=" + name + ", description=" + description + ", comment=" + comment + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
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
		PlaceFeedItem other = (PlaceFeedItem) obj;
		if (id != other.id)
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		}
		else if (!time.equals(other.time))
			return false;
		return true;
	}

}
