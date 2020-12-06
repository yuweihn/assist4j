package com.yuweix.assist4j.core.geo;


import com.yuweix.assist4j.core.MathUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author yuwei
 */
public class GeoHash {
	private Location location;

	/**
	 * 1 2500km; 2 630km; 3 78km; 4 30km;  5   2.4km;
	 * 6 610m;   7 76m;   8 19m;  9 2m;    10  60cm;
	 */
	public static final int DEFAULT_HASH_LENGTH = 10;
	/**
	 * 纬度转化为二进制长度
	 */
	private int latLength = 25;
	/**
	 * 经度转化为二进制长度
	 */
	private int lngLength = 25;

	/**
	 * 每格纬度的单位大小
	 */
	private double minLat;
	/**
	 * 每格经度的大小
	 */
	private double minLng;

	private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7'
							, '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n'
							, 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};


	public GeoHash(double lat, double lng) {
		this(DEFAULT_HASH_LENGTH, lat, lng);
	}
	private GeoHash(int hashLength, double lat, double lng) {
		setHashLength(hashLength);
		location = new Location(lat, lng);
		setMinLatLng();
	}

	/**
	 * 设置经纬度的最小单位
	 */
	private void setMinLatLng() {
		minLat = Location.MAX_LAT - Location.MIN_LAT;
		for (int i = 0; i < latLength; i++) {
			minLat = MathUtil.mul(minLat, 0.5);
		}
		minLng = Location.MAX_LNG - Location.MIN_LNG;
		for (int i = 0; i < lngLength; i++) {
			minLng = MathUtil.mul(minLng, 0.5);
		}
	}

	/**
	 * 求所在坐标点及周围点组成的九个
	 */
	public List<String> getGeoHashBase32For9() {
		double leftLat = MathUtil.sub(location.getLat(), minLat);
		double rightLat = MathUtil.add(location.getLat(), minLat);
		double upLng = MathUtil.sub(location.getLng(), minLng);
		double downLng = MathUtil.add(location.getLng(), minLng);
		List<String> base32For9 = new ArrayList<String>();
		//左侧从上到下 3个
		String leftUp = getGeoHashBase32(leftLat, upLng);
		if (!(leftUp == null || "".equals(leftUp))) {
			base32For9.add(leftUp);
		}
		String leftMid = getGeoHashBase32(leftLat, location.getLng());
		if (!(leftMid == null || "".equals(leftMid))) {
			base32For9.add(leftMid);
		}
		String leftDown = getGeoHashBase32(leftLat, downLng);
		if (!(leftDown == null || "".equals(leftDown))) {
			base32For9.add(leftDown);
		}
		//中间从上到下 3个
		String midUp = getGeoHashBase32(location.getLat(), upLng);
		if (!(midUp == null || "".equals(midUp))) {
			base32For9.add(midUp);
		}
		String midMid = getGeoHashBase32(location.getLat(), location.getLng());
		if (!(midMid == null || "".equals(midMid))) {
			base32For9.add(midMid);
		}
		String midDown = getGeoHashBase32(location.getLat(), downLng);
		if (!(midDown == null || "".equals(midDown))) {
			base32For9.add(midDown);
		}
		//右侧从上到下 3个
		String rightUp = getGeoHashBase32(rightLat, upLng);
		if (!(rightUp == null || "".equals(rightUp))) {
			base32For9.add(rightUp);
		}
		String rightMid = getGeoHashBase32(rightLat, location.getLng());
		if (!(rightMid == null || "".equals(rightMid))) {
			base32For9.add(rightMid);
		}
		String rightDown = getGeoHashBase32(rightLat, downLng);
		if (!(rightDown == null || "".equals(rightDown))) {
			base32For9.add(rightDown);
		}
		return base32For9;
	}

	/**
	 * 设置经纬度转化为geohash长度
	 */
	private boolean setHashLength(int length) {
		if (length < 1) {
			return false;
		}
		latLength = (length * 5) / 2;
		if (length % 2 == 0) {
			lngLength = latLength;
		} else {
			lngLength = latLength + 1;
		}
		setMinLatLng();
		return true;
	}

	/**
	 * 获取经纬度的base32字符串
	 */
	public String getGeoHashBase32() {
		return getGeoHashBase32(location.getLat(), location.getLng());
	}

	/**
	 * 获取经纬度的base32字符串
	 */
	private String getGeoHashBase32(double lat, double lng) {
		boolean[] bools = getGeoBinary(lat, lng);
		if (bools == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < bools.length; i = i + 5) {
			boolean[] base32 = new boolean[5];
			for (int j = 0; j < 5; j++) {
				base32[j] = bools[i + j];
			}
			char cha = getBase32Char(base32);
			if (' ' == cha) {
				return null;
			}
			builder.append(cha);
		}
		return builder.toString();
	}

	/**
	 * 将五位二进制转化为base32
	 */
	private char getBase32Char(boolean[] base32) {
		if (base32 == null || base32.length != 5) {
			return ' ';
		}
		int num = 0;
		for (boolean bool : base32) {
			num <<= 1;
			if (bool) {
				num += 1;
			}
		}
		return CHARS[num % CHARS.length];
	}

	/**
	 * 获取坐标的geo二进制字符串
	 */
	private boolean[] getGeoBinary(double lat, double lng) {
		boolean[] latArray = getHashArray(lat, Location.MIN_LAT, Location.MAX_LAT, latLength);
		boolean[] lngArray = getHashArray(lng, Location.MIN_LNG, Location.MAX_LNG, lngLength);
		return merge(latArray, lngArray);
	}

	/**
	 * 合并经纬度二进制
	 */
	private boolean[] merge(boolean[] latArray, boolean[] lngArray) {
		if (latArray == null || lngArray == null) {
			return null;
		}
		boolean[] result = new boolean[lngArray.length + latArray.length];
		Arrays.fill(result, false);
		for (int i = 0; i < lngArray.length; i++) {
			result[2 * i] = lngArray[i];
		}
		for (int i = 0; i < latArray.length; i++) {
			result[2 * i + 1] = latArray[i];
		}
		return result;
	}

	/**
	 * 将数字转化为geohash二进制字符串
	 */
	private boolean[] getHashArray(double value, double min, double max, int length) {
		if (value < min || value > max) {
			return null;
		}
		if (length < 1) {
			return null;
		}
		boolean[] result = new boolean[length];
		for (int i = 0; i < length; i++) {
			double mid = MathUtil.mul(MathUtil.add(min, max), 0.5);
			if (value > mid) {
				result[i] = true;
				min = mid;
			} else {
				result[i] = false;
				max = mid;
			}
		}
		return result;
	}
}
