package com.yuweix.assist4j.core;


import java.math.BigDecimal;


/**
 * @author yuwei
 */
public abstract class MathUtil {
	/**
	 * 默认除法运算精度
	 */
	private static final int DEFAULT_DIV_SCALE = 2;
	/**
	 * 地球赤道半径(km)
	 */
	private static final double EARTH_RADIUS = 6378.137;



	/**
	 * 将浮点数强转为整数，小数部分往上加。
	 * 如4.2 ====== 5
	 * @return
	 */
	public static int ceil(double d) {
		int i = (int) d;
		BigDecimal d0 = new BigDecimal(Double.toString(d));
		BigDecimal i0 = new BigDecimal(Double.toString(i));
		return d0.compareTo(i0) == 0 ? i : (i + 1);
	}

	/**
	 * 浮点数加法
	 * @return
	 */
	public static double add(double... params) {
		if (params == null || params.length < 2) {
			throw new RuntimeException("The number of params can not be less then two.");
		}

		BigDecimal b = new BigDecimal("0");
		for (double num: params) {
			b = b.add(new BigDecimal(Double.toString(num)));
		}
		return b.doubleValue();
	}

	/**
	 * 浮点数减法
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 浮点数乘法
	 * @return
	 */
	public static double mul(double... params) {
		if (params == null || params.length < 2) {
			throw new RuntimeException("The number of params can not be less then two.");
		}

		BigDecimal b = new BigDecimal("1");
		for (double num: params) {
			b = b.multiply(new BigDecimal(Double.toString(num)));
		}
		return b.doubleValue();
	}

	/**
	 * 浮点数除法
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEFAULT_DIV_SCALE);
	}

	/**
	 * 浮点数除法
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero.");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 取指定闭区间范围内的随机数，[min, max]
	 */
	public static int genRandNumber(int min, int max) {
		if (min > max) {
			throw new RuntimeException("[min] can not be larger than [max].");
		}
		return min + (int) (Math.random() * (max - min + 1));
	}

	/**
	 * 计算弧度(180° ====== π)
	 */
	public static double rad(double degree) {
		return mul(Math.PI, degree, 1 / 180.0);
	}

	/**
	 * 计算地球上两点间距离(km)
	 */
	public static double getDistance(double lon1, double lat1, double lon2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = sub(radLat1, radLat2);
		double b = sub(rad(lon1), rad(lon2));

		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		distance = mul(EARTH_RADIUS, distance);
		distance = div(distance, 1.0, 3);
		return distance;
	}

	/**
	 * 取下一个素数
	 */
	public static int findNextPrime(int n) {
		if (n < 2) {
			return 2;
		}

		int next = n + 1;
		do {
			int sqrt = (int) Math.sqrt(next);
			int i = 2;
			for (; i <= sqrt; i++) {
				if (next % i == 0) {
					break;
				}
			}

			if (i > sqrt) {
				return next;
			} else {
				next++;
			}
		} while (true);
	}
}
