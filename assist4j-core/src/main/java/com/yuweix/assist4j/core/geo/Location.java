package com.yuweix.assist4j.core.geo;



/**
 * @author yuwei
 */
public class Location {
	public static final double MIN_LAT = -90;
	public static final double MAX_LAT = 90;
	public static final double MIN_LNG = -180;
	public static final double MAX_LNG = 180;
	/**
	 * 纬度[-90, 90]
	 */
	private double lat;
	/**
	 * 经度[-180, 180]
	 */
	private double lng;


	public Location(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}


	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
}
