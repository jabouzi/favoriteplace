package com.skanderjabouzi.favoriteplace;

import java.io.Serializable;

public class Favorite implements Serializable{

	private long id;
	private float latitude;
	private float longitude;
	private String country;
	private String city;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
