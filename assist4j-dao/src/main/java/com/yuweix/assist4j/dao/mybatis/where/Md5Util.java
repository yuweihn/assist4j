package com.yuweix.assist4j.dao.mybatis.where;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;


/**
 * @author yuwei
 */
abstract class Md5Util {
	private static final Logger log = LoggerFactory.getLogger(Md5Util.class);
	private static final char HEX_DIGIT[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


	public static String getMd5(String str) {
		try {
			byte[] tmp = str.getBytes("utf-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(tmp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char arr[] = new char[j << 1];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				arr[k++] = HEX_DIGIT[byte0 >>> 4 & 0xf];
				arr[k++] = HEX_DIGIT[byte0 & 0xf];
			}
			String val = new String(arr).toLowerCase();
			log.debug(val);
			return val;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
