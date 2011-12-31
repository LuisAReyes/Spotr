package com.csun.spotr.core;

public class Reward {
	// required parameters
	private final int id;
	private final String name;
	private final String date;
	private final String url;
	
	// optional parameters
	private String description;
	
	public static class Builder {
		// required parameters
		private final int id;
		private final String name;
		private final String date;
		private final String url;
		
		// optional parameters
		private String description = "n/a";
		
		public Builder(int id, String name, String date, String url) {
			this.id = id;
			this.name = name;
			this.date = date;
			this.url = url;
		}
		
		public Reward build() {
			return new Reward(this);
		}
	}
	
	public Reward(Builder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.date = builder.date;
		this.url = builder.url;
		this.description = builder.description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + id;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Reward other = (Reward) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		}
		else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		}
		else if (!url.equals(other.url))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Reward [id=" + id + ", name=" + name + ", date=" + date + ", url=" + url + ", description=" + description + "]";
	}
}
