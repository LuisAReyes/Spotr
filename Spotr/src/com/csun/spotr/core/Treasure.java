package com.csun.spotr.core;

public class Treasure {
	private final int id;

	private String name;
	private String iconUrl;
	private String type;
	private String code;
	private String company;
	private String expirationDate;

	public Treasure(int id, String name, String iconUrl, String type, String code, String company, String expirationDate) {
		this.id = id;
		this.name = name;
		this.iconUrl = iconUrl;
		this.type = type;
		this.code = code;
		this.company = company;
		this.expirationDate = expirationDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getId() {
		return id;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + id;
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
		Treasure other = (Treasure) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		}
		else if (!code.equals(other.code))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Treasure [id=" + id + ", name=" + name + ", iconUrl=" + iconUrl + ", type=" + type + ", code=" + code + ", company=" + company + ", expirationDate=" + expirationDate + "]";
	}
}
