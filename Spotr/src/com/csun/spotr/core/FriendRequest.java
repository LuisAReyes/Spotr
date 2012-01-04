package com.csun.spotr.core;

public class FriendRequest {
	private int friendId;
	private String friendName;
	private String message;
	private String time;

	public FriendRequest(int friendId, String friendName, String message, String time) {
		this.friendId = friendId;
		this.friendName = friendName;
		this.message = message;
		this.time = time;
	}
	
	public int getFriendId() {
		return friendId;
	}

	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}

	public String getFriendName() {
		return friendName;
	}

	@Override
	public String toString() {
		return friendName + "has sent you a request with message \"" + message + "\"";
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
