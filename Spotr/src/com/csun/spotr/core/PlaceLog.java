package com.csun.spotr.core;

import android.widget.TextView.BufferType;

import com.csun.spotr.gui.UserActivityItemAdapter;

public class PlaceLog {
	// required parameters
	private final int id;
	private final String username;
	private final String challengeType;
	private final String time;

	// optional parameters
	private String name;
	private String description;
	private String imageUrl;
	private String userUrl;
	private String comment;
	
	public static class Builder {
		// required parameters
		private final int id;
		private final String username;
		private final String challengeType;
		private final String time;

		// optional parameters
		private String name = "";
		private String description = "";
		private String imageUrl = "";
		private String userUrl = "";
		private String comment = "";
		
		public Builder(int id, String username, String challengeType, String time) {
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
		
		public Builder imageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}
		
		public Builder userUrl(String userUrl) {
			this.userUrl = userUrl;
			return this;
		}
		
		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}
		
		public PlaceLog build() {
			return new PlaceLog(this);
		}
	}
	
	public PlaceLog(Builder builder) {
		this.id = builder.id;
		this.username = builder.username;
		this.challengeType = builder.challengeType;
		this.time = builder.time;
		this.name = builder.name;
		this.description = builder.description;
		this.imageUrl = builder.imageUrl;
		this.userUrl = builder.userUrl;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
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

	public String getChallengeType() {
		return challengeType;
	}

	public String getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "UserActivity [id=" + id + ", username=" + username + ", challengeType=" + challengeType + ", time=" + time + ", name=" + name + ", description=" + description + ", imageUrl=" + imageUrl + ", comment=" + comment + "]";
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
		PlaceLog other = (PlaceLog) obj;
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
