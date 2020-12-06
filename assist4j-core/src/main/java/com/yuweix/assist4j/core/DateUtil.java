package com.yuweix.assist4j.core;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @author wei
 */
public abstract class DateUtil {
	/**
	 * yyyy-MM
	 */
	public static final String PATTERN_YM = "yyyy-MM";
	/**
	 * yyyy-MM-dd
	 */
	public static final String PATTERN_DATE1 = "yyyy-MM-dd";
	/**
	 * yyyy/MM/dd
	 */
	public static final String PATTERN_DATE2 = "yyyy/MM/dd";
	/**
	 * yyyy年MM月dd日
	 */
	public static final String PATTERN_DATE3 = "yyyy年MM月dd日";
	/**
	 * yyyyMMdd
	 */
	public static final String PATTERN_DATE4 = "yyyyMMdd";
	/**
	 * HH:mm
	 */
	public static final String PATTERN_TIME1 = "HH:mm";
	/**
	 * HH:mm:ss
	 */
	public static final String PATTERN_TIME2 = "HH:mm:ss";
	/**
	 * yyyy-MM-dd'T'HH:mm:ss
	 */
	public static final String PATTERN_DATE_TIME1 = "yyyy-MM-dd'T'HH:mm:ss";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String PATTERN_DATE_TIME2 = "yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String PATTERN_DATE_TIME3 = "yyyyMMddHHmmss";
	/**
	 * yyyyMMddHHmm
	 */
	public static final String PATTERN_DATE_TIME4 = "yyyyMMddHHmm";
	/**
	 * yyyyMMddHH
	 */
	public static final String PATTERN_DATE_TIME5 = "yyyyMMddHH";
	/**
	 * yyyyMMddHHmmssSSS
	 */
	public static final String PATTERN_DATE_TIME6 = "yyyyMMddHHmmssSSS";
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public static final String PATTERN_DATE_TIME7 = "yyyy-MM-dd HH:mm";
	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static final String PATTERN_DATE_TIME8 = "yyyy-MM-dd HH:mm:ss.SSS";


	/**
	 * 时间单位。年
	 */
	public static final int UNIT_YEAR = 1;
	/**
	 * 时间单位。月
	 */
	public static final int UNIT_MONTH = 2;
	/**
	 * 时间单位。周
	 */
	public static final int UNIT_WEEK = 3;
	/**
	 * 时间单位。天
	 */
	public static final int UNIT_DAY = 4;
	/**
	 * 时间单位。时
	 */
	public static final int UNIT_HOUR = 5;
	/**
	 * 时间单位。分
	 */
	public static final int UNIT_MINUTE = 6;
	/**
	 * 时间单位。秒
	 */
	public static final int UNIT_SECOND = 7;



	/**
	 * 获取当前年份
	 * @return
	 */
	public static int getCurYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 按默认格式(yyyy-MM-dd)格式化日期
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return new SimpleDateFormat(PATTERN_DATE1).format(date);
	}

	/**
	 * 按指定格式格式化日期
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 按指定格式解析日期
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parseDate(String dateStr, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取指定日期在指定时间长度之后的日期。
	 * @param date
	 * @param offset
	 * @param unit
	 * @return
	 */
	public static Date getDateOffset(Date date, int offset, int unit) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		if (offset != 0) {
			switch (unit) {
				case UNIT_YEAR:
					cal.add(Calendar.YEAR, offset);
					break;
				case UNIT_MONTH:
					cal.add(Calendar.MONTH, offset);
					break;
				case UNIT_WEEK:
					cal.add(Calendar.WEEK_OF_YEAR, offset);
					break;
				case UNIT_DAY:
					cal.add(Calendar.DAY_OF_YEAR, offset);
					break;
				case UNIT_HOUR:
					cal.add(Calendar.HOUR_OF_DAY, offset);
					break;
				case UNIT_MINUTE:
					cal.add(Calendar.MINUTE, offset);
					break;
				case UNIT_SECOND:
					cal.add(Calendar.SECOND, offset);
					break;
			}
		}
		return cal.getTime();
	}

	/**
	 * 获取“年”
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}


	/**
	 * 获取“月”
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取“日”
	 * @param date
	 * @return
	 */
	public static int getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取“小时”
	 * @param date
	 * @return
	 */
	public static int getHour(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取“分钟”
	 * @param date
	 * @return
	 */
	public static int getMinute(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * 获取“秒”
	 * @param date
	 * @return
	 */
	public static int getSecond(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.SECOND);
	}

	/**
	 * 取得指定日期的第二天
	 * @param date
	 * @return
	 */
	public static Date getNextDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/**
	 * 取得指定日期的00:00:00.000
	 * @param date
	 * @return
	 */
	public static Date getStartOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 取得指定日期的最后一秒
	 * @param date
	 * @return
	 */
	public static Date getLastSecOfDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 判断两个日期是否是同一天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		int y1 = getYear(date1);
		int m1 = getMonth(date1);
		int d1 = getDay(date1);
		
		int y2 = getYear(date2);
		int m2 = getMonth(date2);
		int d2 = getDay(date2);
		
		return (y1 == y2) && (m1 == m2) && (d1 == d2);
	}

	/**
	 * 验证日期是否合法
	 * @param year
	 * @param month
	 * @param day
	 */
	public static void validate(int year, int month, int day) {
		if (year < 1) {
			throw new IllegalArgumentException("年份不能小于1");
		}
		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("月份只能取值1~12");
		}

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, 1);

		c.add(Calendar.DAY_OF_MONTH, -1);

		int lastDay = c.get(Calendar.DAY_OF_MONTH);
		if (day < 1 || day > lastDay) {
			throw new IllegalArgumentException("日取值不能超过" + 1 + "~" + lastDay);
		}
	}

	/**
	 * 计算两个日期相差的天数
	 * 若date1比date2小，返回正数；
	 * 若date1比date2大，返回负数；
	 * 若相等，返回0
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getDayDiff(Date date1, Date date2) {
		long dif = (date2.getTime() - date1.getTime()) / (1000L * 60 * 60 * 24);
		return (int) dif;
	}
	
//	public static String showTime(Date date) {
//		String resp = "";
//		if (date == null) {
//			return resp;
//		}
//		
//		Date now = Calendar.getInstance().getTime();
//		if (DateUtil.isSameDay(now, date)) {
//			long sec = (now.getTime() - date.getTime()) / 1000;
//			if (sec < 0) {
//				return "";
//			}
//			
//			if (sec < 60) {
//				/**
//				 * 1分钟内
//				 */
//				return sec + "秒前";
//			}
//			if (sec < 60 * 60) {
//				/**
//				 * 1小时内
//				 */
//				int m = (int) (sec / 60);
//				return m + "分钟前";
//			}
//			/**
//			 * 1天内
//			 */
//			int h = (int) (sec / (60 * 60));
//			return h + "小时前";
//		}
//		
//		int diff = DateUtil.getDayDiff(DateUtil.getStartOfDate(date), DateUtil.getStartOfDate(now));
//		if (diff <= 0) {
//			return "";
//		}
//		if (diff == 1) {
//			return "昨天";
//		}
//		if (diff < 7) {
//			return diff + "天前";
//		}
//		
//		return DateUtil.formatDate(date, "yyyy-MM-dd");
//	}
}
