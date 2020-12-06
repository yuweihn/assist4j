package com.yuweix.assist4j.core.encrypt;


import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author yuwei
 */
public abstract class DSACoder {
	public static final String ALGORITHM = "DSA";
	/**
	 * 默认密钥字节数
	 *
	 * <pre>
	 *
	 * DSA  
	 * Default Keysize 1024   
	 * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive).
	 * </pre>
	 */
	private static final int KEY_SIZE = 1024;


	private static final String PUBLIC_KEY = "DSAPublicKey";
	private static final String PRIVATE_KEY = "DSAPrivateKey";



	/**
	 * 生成密钥
	 *
	 * @return 密钥对象
	 * @throws Exception
	 */
	public static Map<String, String> initKey() throws Exception {
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.setSeed(UUID.randomUUID().toString().replace("-", "").getBytes());
		KeyPairGenerator keygen = KeyPairGenerator.getInstance(ALGORITHM);
		keygen.initialize(KEY_SIZE, secureRandom);

		KeyPair kPair = keygen.genKeyPair();
		PublicKey publicKey = kPair.getPublic();
		PrivateKey privateKey = kPair.getPrivate();

		Map<String, String> map = new HashMap<String, String>(2);
		map.put(PUBLIC_KEY, byte2HexStr(publicKey.getEncoded()));
		map.put(PRIVATE_KEY, byte2HexStr(privateKey.getEncoded()));

		return map;
	}

	/**
	 * 用私钥对信息生成数字签名
	 *
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 *
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(hexStr2Byte(privateKey));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		PrivateKey priKey = keyFactory.generatePrivate(spec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
		signature.initSign(priKey);
		signature.update(data);

		return byte2HexStr(signature.sign());
	}

	/**
	 * 用公钥校验数字签名
	 *
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 *
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 *
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(hexStr2Byte(publicKey));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(keyFactory.getAlgorithm());
		signature.initVerify(pubKey);
		signature.update(data);

		// 验证签名是否正常
		return signature.verify(hexStr2Byte(sign));
	}

	/**
	 * 取得私钥
	 *
	 * @param keyMap
	 * @return
	 */
	private static String getPrivateKey(Map<String, String> keyMap) {
		return keyMap.get(PRIVATE_KEY);
	}

	/**
	 * 取得公钥
	 *
	 * @param keyMap
	 * @return
	 */
	private static String getPublicKey(Map<String, String> keyMap) {
		return keyMap.get(PUBLIC_KEY);
	}

	/**
	 * 将二进制转换成16进制
	 * @param value
	 * @return
	 */
	private static String byte2HexStr(byte[] value) {
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

	/**
	 * 将16进制转换为二进制
	 * @param value
	 * @return
	 */
	private static byte[] hexStr2Byte(String value) {
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



	public static void main(String... args) throws Exception {
		String inputStr = "yuweitest";
		byte[] data = inputStr.getBytes();

		// 构建密钥
		Map<String, String> keyMap = DSACoder.initKey();

		// 获得密钥
		String publicKey = DSACoder.getPublicKey(keyMap);
		String privateKey = DSACoder.getPrivateKey(keyMap);

		System.out.println("公钥: " + publicKey);
		System.out.println("私钥: " + privateKey);

		// 产生签名
		String sign = DSACoder.sign(data, privateKey);
		System.out.println("签名: " + sign);

		// 验证签名
		boolean status = DSACoder.verify(data, publicKey, sign);
		System.out.println("状态: " + status);
	}
}
