package com.yuweix.assist4j.core;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 身份证工具类
 * @author yuwei
 */
public abstract class IdCardUtil {
	private static final Logger log = LoggerFactory.getLogger(IdCardUtil.class);
	/** 中国公民一代身份证号码长度。 */
	private static final int CHINA_ID_NO_1_LENGTH = 15;
	/** 中国公民二代身份证号码长度。 */
	private static final int CHINA_ID_NO_2_LENGTH = 18;
	/** 加权因子 */
	private static final int WEIGHT_FACTORS[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
	private static Map<String, String> cityCodes = new HashMap<String, String>();
	/** 台湾身份首字母对应数字 */
	private static Map<String, Integer> twFirstCode = new HashMap<String, Integer>();
	/** 香港身份首字母对应数字 */
	private static Map<String, Integer> hkFirstCode = new HashMap<String, Integer>();


	static {
		cityCodes.put("11", "北京市");
		cityCodes.put("12", "天津市");
		cityCodes.put("13", "河北省");
		cityCodes.put("14", "山西省");
		cityCodes.put("15", "内蒙古自治区");
		cityCodes.put("21", "辽宁省");
		cityCodes.put("22", "吉林省");
		cityCodes.put("23", "黑龙江省");
		cityCodes.put("31", "上海市");
		cityCodes.put("32", "江苏省");
		cityCodes.put("33", "浙江省");
		cityCodes.put("34", "安徽省");
		cityCodes.put("35", "福建省");
		cityCodes.put("36", "江西省");
		cityCodes.put("37", "山东省");
		cityCodes.put("41", "河南省");
		cityCodes.put("42", "湖北省");
		cityCodes.put("43", "湖南省");
		cityCodes.put("44", "广东省");
		cityCodes.put("45", "广西壮族自治区");
		cityCodes.put("46", "海南省");
		cityCodes.put("50", "重庆市");
		cityCodes.put("51", "四川省");
		cityCodes.put("52", "贵州省");
		cityCodes.put("53", "云南省");
		cityCodes.put("54", "西藏自治区");
		cityCodes.put("61", "陕西省");
		cityCodes.put("62", "甘肃省");
		cityCodes.put("63", "青海省");
		cityCodes.put("64", "宁夏回族自治区");
		cityCodes.put("65", "新疆维吾尔族自治区");
		cityCodes.put("71", "台湾");
		cityCodes.put("81", "香港");
		cityCodes.put("82", "澳门");
		cityCodes.put("91", "国外");
		twFirstCode.put("A", 10);
		twFirstCode.put("B", 11);
		twFirstCode.put("C", 12);
		twFirstCode.put("D", 13);
		twFirstCode.put("E", 14);
		twFirstCode.put("F", 15);
		twFirstCode.put("G", 16);
		twFirstCode.put("H", 17);
		twFirstCode.put("J", 18);
		twFirstCode.put("K", 19);
		twFirstCode.put("L", 20);
		twFirstCode.put("M", 21);
		twFirstCode.put("N", 22);
		twFirstCode.put("P", 23);
		twFirstCode.put("Q", 24);
		twFirstCode.put("R", 25);
		twFirstCode.put("S", 26);
		twFirstCode.put("T", 27);
		twFirstCode.put("U", 28);
		twFirstCode.put("V", 29);
		twFirstCode.put("X", 30);
		twFirstCode.put("Y", 31);
		twFirstCode.put("W", 32);
		twFirstCode.put("Z", 33);
		twFirstCode.put("I", 34);
		twFirstCode.put("O", 35);
		hkFirstCode.put("A", 1);
		hkFirstCode.put("B", 2);
		hkFirstCode.put("C", 3);
		hkFirstCode.put("R", 18);
		hkFirstCode.put("U", 21);
		hkFirstCode.put("Z", 26);
		hkFirstCode.put("X", 24);
		hkFirstCode.put("W", 23);
		hkFirstCode.put("O", 15);
		hkFirstCode.put("N", 14);
	}

	/**
	 * 将15位身份证号码转换为18位
	 * @param cardNo   15位身份证号码
	 * @return 18位身份编码
	 */
	private static String convert15To18(String cardNo) {
		if (cardNo == null || cardNo.length() != CHINA_ID_NO_1_LENGTH) {
			return null;
		}
		if (!isNum(cardNo)) {
			return null;
		}

		/**
		 * 获取出生年月日
		 */
		String birthday = cardNo.substring(6, 12);
		Calendar cal = Calendar.getInstance();
		try {
			Date birthDate = new SimpleDateFormat("yyMMdd").parse(birthday);
			cal.setTime(birthDate);
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
		/**
		 * 获取出生年(完整表现形式,如：2010)
		 */
		String sYear = String.valueOf(cal.get(Calendar.YEAR));
		String idCard18 = cardNo.substring(0, 6) + sYear + cardNo.substring(8);
		/**
		 * 转换字符数组
		 */
		char[] cArr = idCard18.toCharArray();
		if (cArr == null) {
			return null;
		}
		int[] iCard = convertCharToInt(cArr);
		int iSum17 = getPowerSum(iCard);
		/**
		 * 获取校验位
		 */
		String sVal = getCheckCode18(iSum17);
		if (sVal == null || sVal.length() <= 0) {
			return null;
		}
		return idCard18 + sVal;
	}

	/**
	 * 检查身份证是否合法
	 * @param cardNo 身份证号码
	 */
	public static boolean check(String cardNo) {
		if (cardNo == null) {
			return false;
		}
		String cardNo0 = cardNo.trim();
		if (check18(cardNo0)) {
			return true;
		}
		if (check15(cardNo0)) {
			return true;
		}
		return false;
	}

	/**
	 * 验证18位身份证号码是否合法
	 * @param cardNo 身份证号码
	 */
	private static boolean check18(String cardNo) {
		if (cardNo == null || cardNo.length() != CHINA_ID_NO_2_LENGTH) {
			return false;
		}

		// 前17位
		String code17 = cardNo.substring(0, 17);
		// 第18位
		String code18 = cardNo.substring(17, CHINA_ID_NO_2_LENGTH);
		if (!isNum(code17)) {
			return false;
		}
		char[] cArr = code17.toCharArray();
		if (cArr == null) {
			return false;
		}
		int[] iCard = convertCharToInt(cArr);
		int iSum17 = getPowerSum(iCard);
		// 获取校验位
		String val = getCheckCode18(iSum17);
		return code18.equalsIgnoreCase(val);
	}

	/**
	 * 验证15位身份证号码是否合法
	 * @param cardNo 身份证号码
	 */
	private static boolean check15(String cardNo) {
		if (cardNo == null || cardNo.length() != CHINA_ID_NO_1_LENGTH) {
			return false;
		}
		if (!isNum(cardNo)) {
			return false;
		}
		
		String proCode = cardNo.substring(0, 2);
		if (cityCodes.get(proCode) == null) {
			return false;
		}
		String birthCode = cardNo.substring(6, 12);
		try {
			Date birthday = new SimpleDateFormat("yyMMdd").parse(birthCode);
			return birthday.before(new Date());
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
	}

	/**
	 * 验证台湾身份证号码
	 * @param cardNo 身份证号码
	 */
	public static boolean checkTwCard(String cardNo) {
		if (cardNo == null) {
			return false;
		}
		String start = cardNo.substring(0, 1);
		String mid = cardNo.substring(1, 9);
		String end = cardNo.substring(9, 10);
		int iStart = twFirstCode.get(start);
		int sum = iStart / 10 + (iStart % 10) * 9;
		char[] chars = mid.toCharArray();
		int iflag = 8;
		for (char c: chars) {
			sum = sum + Integer.valueOf(c + "") * iflag;
			iflag--;
		}
		return (sum % 10 == 0 ? 0 : (10 - sum % 10)) == Integer.valueOf(end) ? true : false;
	}

	/**
	 * 验证香港身份证号码(存在Bug，部份特殊身份证无法检查)
	 * <p>
	 * 身份证前2位为英文字符，如果只出现一个英文字符则表示第一位是空格，对应数字58 前2位英文字符A-Z分别对应数字10-35
	 * 最后一位校验码为0-9的数字加上字符"A"，"A"代表10
	 * </p>
	 * <p>
	 * 将身份证号码全部转换为数字，分别对应乘9-1相加的总和，整除11则证件号码有效
	 * </p>
	 * 
	 * @param cardNo 身份证号码
	 */
	public static boolean checkHkCard(String cardNo) {
		if (cardNo == null) {
			return false;
		}
		String card = cardNo.replaceAll("[\\(|\\)]", "");
		int sum = 0;
		if (card.length() == 9) {
			sum = (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 9
					+ (Integer.valueOf(card.substring(1, 2).toUpperCase().toCharArray()[0]) - 55) * 8;
			card = card.substring(1, 9);
		} else {
			sum = 522 + (Integer.valueOf(card.substring(0, 1).toUpperCase().toCharArray()[0]) - 55) * 8;
		}
		String mid = card.substring(1, 7);
		String end = card.substring(7, 8);
		char[] chars = mid.toCharArray();
		Integer iflag = 7;
		for (char c: chars) {
			sum = sum + Integer.valueOf(c + "") * iflag;
			iflag--;
		}
		if (end.toUpperCase().equals("A")) {
			sum = sum + 10;
		} else {
			sum = sum + Integer.valueOf(end);
		}
		return (sum % 11 == 0) ? true : false;
	}

	/**
	 * 将字符数组转换成数字数组
	 * @param ch 字符数组
	 * @return 数字数组
	 */
	private static int[] convertCharToInt(char[] ch) {
		if (ch == null) {
			return null;
		}
		int len = ch.length;
		int[] iArr = new int[len];
		try {
			for (int i = 0; i < len; i++) {
				iArr[i] = Integer.parseInt(String.valueOf(ch[i]));
			}
			return iArr;
		} catch (NumberFormatException e) {
			log.error("", e);
			return null;
		}
	}

	/**
	 * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
	 * @param iArr
	 * @return 身份证编码。
	 */
	private static int getPowerSum(int[] iArr) {
		if (iArr == null || WEIGHT_FACTORS.length != iArr.length) {
			return 0;
		}

		int iSum = 0;
		for (int i = 0; i < iArr.length; i++) {
			for (int j = 0; j < WEIGHT_FACTORS.length; j++) {
				if (i == j) {
					iSum = iSum + iArr[i] * WEIGHT_FACTORS[j];
				}
			}
		}
		return iSum;
	}

	/**
	 * 将power和值与11取模获得余数进行校验码判断
	 * @param iSum
	 * @return 校验位
	 */
	private static String getCheckCode18(int iSum) {
		String sCode = "";
		switch (iSum % 11) {
			case 10:
				sCode = "2";
				break;
			case 9:
				sCode = "3";
				break;
			case 8:
				sCode = "4";
				break;
			case 7:
				sCode = "5";
				break;
			case 6:
				sCode = "6";
				break;
			case 5:
				sCode = "7";
				break;
			case 4:
				sCode = "8";
				break;
			case 3:
				sCode = "9";
				break;
			case 2:
				sCode = "x";
				break;
			case 1:
				sCode = "0";
				break;
			case 0:
				sCode = "1";
				break;
		}
		return sCode;
	}

	/**
	 * 根据18位身份证号码获取生日
	 * @param cardNo 身份证号码
	 * @return 生日(yyyyMMdd)
	 */
	public static String getBirthdayByCardNo(String cardNo) {
		if (cardNo == null) {
			return null;
		}
		int len = cardNo.length();
		if (len != CHINA_ID_NO_1_LENGTH && len != CHINA_ID_NO_2_LENGTH) {
			return null;
		}
		
		if (len == CHINA_ID_NO_1_LENGTH) {
			cardNo = convert15To18(cardNo);
		}
		return cardNo.substring(6, 14);
	}

	/**
	 * 根据身份证号码获取性别
	 * @param cardNo 身份证号码
	 * @return 性别(M-男，F-女，U-不详)
	 */
	public static String getGenderByCardNo(String cardNo) {
		String sGender = "U";
		if (cardNo == null) {
			return sGender;
		}
		/**
		 * 场景一：台湾
		 */
		if (Arrays.binarySearch(new long[] {8, 9, 10}, cardNo.length()) >= 0) {
			if (cardNo.matches("^[a-zA-Z][0-9]{9}$")) {
				String char2 = cardNo.substring(1, 2);
				if (char2.equals("1")) {
					sGender = "M";
				} else if (char2.equals("2")) {
					sGender = "F";
				}
			}
			return sGender;
		}

		/**
		 * 场景二：大陆
		 */
		if (cardNo.length() != CHINA_ID_NO_1_LENGTH && cardNo.length() != CHINA_ID_NO_2_LENGTH) {
			return sGender;
		}
		
		if (cardNo.length() == CHINA_ID_NO_1_LENGTH) {
			cardNo = convert15To18(cardNo);
		}
		String sCardNum = cardNo.substring(16, 17);
		if (Integer.parseInt(sCardNum) % 2 != 0) {
			sGender = "M";
		} else {
			sGender = "F";
		}
		return sGender;
	}

	/**
	 * 根据身份证号码获取户籍省份
	 * @param cardNo 身份证号码
	 * @return 省级编码。
	 */
	public static String getProvinceByCardNo(String cardNo) {
		if (cardNo == null) {
			return null;
		}
		int len = cardNo.length();
		String sProvince = null;
		String sProvinceCode = "";
		if (len == CHINA_ID_NO_1_LENGTH || len == CHINA_ID_NO_2_LENGTH) {
			sProvinceCode = cardNo.substring(0, 2);
		}
		sProvince = cityCodes.get(sProvinceCode);
		return sProvince;
	}

	/**
	 * 数字验证
	 * @param val
	 */
	private static boolean isNum(String val) {
		return val == null || "".equals(val) ? false : val.matches("^[0-9]*$");
	}
}
