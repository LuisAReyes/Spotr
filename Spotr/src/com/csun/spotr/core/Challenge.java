package com.csun.spotr.core;

public class Challenge {
	static public enum Type {
		CHECK_IN, 
		SNAP_PICTURE, 
		WRITE_ON_WALL, 
		QUESTION_ANSWER,
		OTHER
	};
	
	public static Type returnType(String typeStr) {
		if (typeStr.equals("CHECK_IN"))
			return Type.CHECK_IN;
		else if (typeStr.equals("SNAP_PICTURE"))
			return Type.SNAP_PICTURE;
		else if (typeStr.equals("WRITE_ON_WALL"))
			return Type.WRITE_ON_WALL;
		else if (typeStr.equals("QUESTION_ANSWER"))
			return Type.QUESTION_ANSWER;
		else // (typeStr.equals("OTHER")) 
			return Type.OTHER;
	}
	
	private final int id;
	private final Type type;
	private final int points;
	
	private String name;
	private String description;
	private int rating;
	
	public static class Builder {
		private final int id;
		private final Type type;
		private final int points;
		
		private String name;
		private String description;
		private int rating;
		
		public Builder(int id, Type type, int points) {
			this.id = id;
			this.type = type;
			this.points = points;
			
			name = "n/a";
			description = "n/a";
			rating = 0;
		}
	
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder rating(int rating) {
			this.rating = rating;
			return this;
		}
		
		public Challenge build() {
			return new Challenge(this);
		}
	}
	
	public Challenge(Builder builder) {
		this.id = builder.id;
		this.type = builder.type;
		this.points = builder.points;
		this.name = builder.name;
		this.description = builder.description;
		this.rating = builder.rating;
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

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public int getPoints() {
		return points;
	}

}
