package com.yuweix.assist4j.data.serializer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


/**
 * 序列化工具
 * @author wei
 */
public class DefaultSerializer implements Serializer {
	private static final Logger log = LoggerFactory.getLogger(DefaultSerializer.class);


	/**
	 * 序列化
	 */
	public <T>String serialize(T t) {
		if (t == null) {
			return null;
		}
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(t);
			return toHexString(baos.toByteArray());
		} catch (Exception e) {
			log.error("", e);
			return null;
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 反序列化
	 */
	@SuppressWarnings("unchecked")
	public <T>T deserialize(String str) {
		if (str == null) {
			return null;
		}
		byte[] bt = toByteArray(str);
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(bt);
			ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (Exception e) {
			log.error("", e);
			return null;
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private static String toHexString(byte[] byteArray) {
		if (byteArray == null || byteArray.length < 1) {
			return null;
		}

		final StringBuilder hexString = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] & 0xff) < 0x10)
				hexString.append("0");
			hexString.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return hexString.toString().toLowerCase();
	}

	private static byte[] toByteArray(String hexString) {
		if (hexString == null || "".equals(hexString)) {
			return null;
		}

		hexString = hexString.toLowerCase();
		final byte[] byteArray = new byte[hexString.length() / 2];
		int k = 0;
		for (int i = 0; i < byteArray.length; i++) {
			byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
			byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
			byteArray[i] = (byte) (high << 4 | low);
			k += 2;
		}
		return byteArray;
	}
}
