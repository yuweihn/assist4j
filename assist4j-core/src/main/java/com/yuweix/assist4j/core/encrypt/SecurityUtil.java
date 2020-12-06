package com.yuweix.assist4j.core.encrypt;


import com.yuweix.assist4j.core.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


/**
 * 加密工具
 * @author yuwei
 */
public abstract class SecurityUtil {
	private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);
	private static final String SECURITY_KEY = "sfdfyu8**((^$$$SDSDhHJlSDDsdsvcx234ex,,,cjv.xckv...";
	private static final char HEX_DIGIT[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


	public static final String getMd5(String str) {
		return getMd5(str, Constant.ENCODING_UTF_8);
	}
	public static final String getMd5(String str, String charset) {
		return getSecurityByAlgor(Algor.MD5.getCode(), str, charset);
	}

	public static final String getSha1(String str) {
		return getSha1(str, Constant.ENCODING_UTF_8);
	}
	public static final String getSha1(String str, String charset) {
		return getSecurityByAlgor(Algor.SHA1.getCode(), str, charset);
	}

	public static String getSecurityByAlgor(String algor, String str, String charset) {
		try {
			byte[] tmp = str.getBytes(charset == null ? Constant.ENCODING_UTF_8 : charset);
			MessageDigest mdTemp = MessageDigest.getInstance(algor);
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


	public static String encode(String str) {
		if (str == null) {
			return null;
		}
		String str0 = encrypt(str);

		String ori = str0 + "," + getMd5(SECURITY_KEY);
		String secr = getMd5(ori);
		return encrypt(str0 + "," + secr);
	}

	public static String decode(String str) {
		if (str == null) {
			return null;
		}

		try {
			String[] arr = new String(decrypt(str)).split(",");
			if (arr == null || arr.length != 2) {
				return null;
			}
			if (arr[1].equals(getMd5(arr[0] + "," + getMd5(SECURITY_KEY)))) {
				return new String(decrypt(arr[0]));
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String encrypt(String word) {
		try {
			Cipher encrypt = Cipher.getInstance(Algor.DES.getCode());
			encrypt.init(1, buildDESKey(getMd5(SECURITY_KEY)));
			return bytesToHexStr(encrypt.doFinal(word.getBytes(Constant.ENCODING_UTF_8)));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String decrypt(String word) {
		try {
			Cipher decrypt = Cipher.getInstance(Algor.DES.getCode());
			decrypt.init(2, buildDESKey(getMd5(SECURITY_KEY)));
			return new String(decrypt.doFinal(hexStrToBytes(word)), Constant.ENCODING_UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static SecretKey buildDESKey(String value) throws UnsupportedEncodingException {
		byte[] bval = value.getBytes(Constant.ENCODING_UTF_8);
		byte[] bt = new byte[8];
		for (int i = 0; (i < bt.length) && (i < bval.length); i++) {
			bt[i] = bval[i];
		}
		return new SecretKeySpec(bt, Algor.DES.getCode());
	}

	/**
	 * 十六进制字符串转byte数组
	 * @param value
	 * @return
	 */
	public static byte[] hexStrToBytes(String value) {
		if (value.length() < 1) {
			return null;
		}
		byte[] result = new byte[value.length() / 2];
		for (int i = 0; i < value.length() / 2; i++) {
			int high = Integer.parseInt(value.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(value.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * byte数组转十六进制字符串
	 * @param value
	 * @return
	 */
	public static String bytesToHexStr(byte[] value) {
		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < value.length; i++) {
			String hex = Integer.toHexString(value[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			builder.append(hex.toLowerCase());
		}
		return builder.toString();
	}
}
