package com.csun.spotr.core;

import android.graphics.drawable.Drawable;

public class FriendFeed {
	// required parameters
	private final int activityId; // in case we want to handle the detail of
									// this activity
	private final int friendId;
	private final String friendName;
	private final Challenge.Type challengeType;
	private final String activityTime;
	private final String placeName;

	// optional parameter
	private String challengeName;
	private String challengeDescription;
	private Drawable activitySnapPictureDrawable;
	private Drawable friendPictureDrawable;
	private String activityComment;

	public static class Builder {
		// required parameters
		private final int activityId; // in case we want to handle the detail of
										// this activity
		private final int friendId;
		private final String friendName;
		private final Challenge.Type challengeType;
		private final String activityTime;
		private final String placeName;

		// optional parameter
		private String challengeName;
		private String challengeDescription;
		private Drawable activitySnapPictureDrawable;
		private Drawable friendPictureDrawable;
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

		public Builder activitySnapPictureDrawable(Drawable activitySnapPictureDrawable) {
			this.activitySnapPictureDrawable = activitySnapPictureDrawable;
			return this;
		}

		public Builder friendPictureDrawable(Drawable friendPicDrawable) {
			this.friendPictureDrawable = friendPicDrawable;
			return this;
		}

		public Builder activityComment(String activityComment) {
			this.activityComment = activityComment;
			return this;
		}

		public FriendFeed build() {
			return new FriendFeed(this);
		}
	}

	public FriendFeed(Builder builder) {
		this.activityId = builder.activityId;
		this.friendId = builder.friendId;
		this.friendName = builder.friendName;
		this.challengeType = builder.challengeType;
		this.activityTime = builder.activityTime;
		this.placeName = builder.placeName;

		this.challengeName = builder.challengeName;
		this.challengeDescription = builder.challengeDescription;
		this.activitySnapPictureDrawable = builder.activitySnapPictureDrawable;
		this.friendPictureDrawable = builder.friendPictureDrawable;
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

	public Drawable getActivitySnapPictureDrawable() {
		return activitySnapPictureDrawable;
	}

	public void setActivitySnapPictureDrawable(Drawable activitySnapPictureDrawable) {
		this.activitySnapPictureDrawable = activitySnapPictureDrawable;
	}

	public Drawable getFriendPictureDrawable() {
		return friendPictureDrawable;
	}

	public void setFriendPictureDrawable(Drawable friendPictureDrawable) {
		this.friendPictureDrawable = friendPictureDrawable;
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
		FriendFeed other = (FriendFeed) obj;
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