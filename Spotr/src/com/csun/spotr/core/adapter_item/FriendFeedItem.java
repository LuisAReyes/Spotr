package com.csun.spotr.core.adapter_item;

import com.csun.spotr.core.Challenge;

public class FriendFeedItem {
	// required parameters
	private final int activityId; 
	private final int friendId;
	private final String friendName;
	private final Challenge.Type challengeType;
	private final String activityTime;
	private final String placeName;

	// optional parameter
	private String challengeName;
	private String challengeDescription;
	private String activitySnapPictureUrl = null;
	private String friendPictureUrl = null;
	private String activityComment;

	public static class Builder {
		// required parameters
		private final int activityId; 
		private final int friendId;
		private final String friendName;
		private final Challenge.Type challengeType;
		private final String activityTime;
		private final String placeName;

		// optional parameter
		private String challengeName;
		private String challengeDescription;
		private String activitySnapPictureUrl = null;
		private String friendPictureUrl = null;
		private String activityComment;

		public Builder(int activityId, int friendId, String friendName, Challenge.Type challengeType, String activityTime, String placeName) {
			this.activityId = activityId;
			this.friendId = friendId;
			this.friendName = friendName;
			this.challengeType = challengeType;
			this.activityTime = activityTime;
			this.placeName = placeName;
		}

		public Builder challengeName(String challengeName) {
			this.challengeName = challengeName;
			return this;
		}

		public Builder challengeDescription(String challengeDescription) {
			this.challengeDescription = challengeDescription;
			return this;
		}

		public Builder activitySnapPictureUrl(String url) {
			this.activitySnapPictureUrl = url;
			return this;
		}

		public Builder friendPictureUrl(String url) {
			this.friendPictureUrl = url;
			return this;
		}

		public Builder activityComment(String activityComment) {
			this.activityComment = activityComment;
			return this;
		}

		public FriendFeedItem build() {
			return new FriendFeedItem(this);
		}
	}

	public FriendFeedItem(Builder builder) {
		this.activityId = builder.activityId;
		this.friendId = builder.friendId;
		this.friendName = builder.friendName;
		this.challengeType = builder.challengeType;
		this.activityTime = builder.activityTime;
		this.placeName = builder.placeName;

		this.challengeName = builder.challengeName;
		this.challengeDescription = builder.challengeDescription;
		this.activitySnapPictureUrl = builder.activitySnapPictureUrl;
		this.friendPictureUrl = builder.friendPictureUrl;
		this.activityComment = builder.activityComment;
	}

	public String getChallengeName() {
		return challengeName;
	}

	public void setChallengeName(String challengeName) {
		this.challengeName = challengeName;
	}

	public String getChallengeDescription() {
		return challengeDescription;
	}

	public void setChallengeDescription(String challengeDescription) {
		this.challengeDescription = challengeDescription;
	}

	public String getActivitySnapPictureUrl() {
		return activitySnapPictureUrl;
	}

	public void setActivitySnapPictureUri(String url) {
		this.activitySnapPictureUrl = url;
	}

	public String getFriendPictureUrl() {
		return friendPictureUrl;
	}

	public void setFriendPictureUrl(String url) {
		this.friendPictureUrl = url;
	}

	public String getActivityComment() {
		return activityComment;
	}

	public void setActivityComment(String activityComment) {
		this.activityComment = activityComment;
	}

	public int getActivityId() {
		return activityId;
	}

	public int getFriendId() {
		return friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	public Challenge.Type getChallengeType() {
		return challengeType;
	}

	public String getActivityTime() {
		return activityTime;
	}

	public String getPlaceName() {
		return placeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + activityId;
		result = prime * result + ((activityTime == null) ? 0 : activityTime.hashCode());
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
		FriendFeedItem other = (FriendFeedItem) obj;
		if (activityId != other.activityId)
			return false;
		if (activityTime == null) {
			if (other.activityTime != null)
				return false;
		}
		else if (!activityTime.equals(other.activityTime))
			return false;
		return true;
	}
}