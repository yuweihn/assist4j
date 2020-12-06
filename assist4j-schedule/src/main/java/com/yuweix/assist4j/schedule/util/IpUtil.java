package com.yuweix.assist4j.schedule.util;


import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author yuwei
 */
public class IpUtil {
	/**
	 * 获取本机内网IP
	 */
	public static String getLocalInnerIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}
