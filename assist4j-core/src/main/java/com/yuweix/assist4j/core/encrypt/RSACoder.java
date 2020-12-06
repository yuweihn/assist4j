package com.yuweix.assist4j.core.encrypt;


import com.yuweix.assist4j.core.Constant;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author yuwei
 */
public abstract class RSACoder {
	public static final String ALGORITHM = "RSA";
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
	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";



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
	 * 加密
	 * @param str
	 * @param publicKey
	 * @return
	 */
	public static String encrypt(String str, String publicKey) throws Exception {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(hexStr2Byte(publicKey));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(spec);

		BigInteger e = pubKey.getPublicExponent();
		BigInteger n = pubKey.getModulus();

		// 获取明文m
		byte ptext[] = str.getBytes(Constant.ENCODING_UTF_8);
		BigInteger m = new BigInteger(ptext);
		// 计算密文c
		BigInteger c = m.modPow(e, n);
		return c.toString();
	}

	/**
	 * 解密
	 * @param str
	 * @param privateKey
	 * @return
	 */
	public static String decrypt(String str, String privateKey) throws Exception {
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(hexStr2Byte(privateKey));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
		RSAPrivateKey priKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);

		// 读取密文
		BigInteger c = new BigInteger(str);
		// 读取私钥
		BigInteger d = priKey.getPrivateExponent();
		// 获取私钥参数及解密
		BigInteger n = priKey.getModulus();
		BigInteger m = c.modPow(d, n);
		byte[] mt = m.toByteArray();
		return new String(mt, Constant.ENCODING_UTF_8);
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

	public static void main(String args[]) throws Exception {
		Map<String, String> keyMap = initKey();
		String publicKey = getPublicKey(keyMap);
		String privateKey = getPrivateKey(keyMap);
//		String publicKey = "30819f300d06092a864886f70d010101050003818d00308189028181009ed67f3ace694e4a4c1e7dbc0a005a85d775eba4b4a88b3c42086d126cc298a087c35537b79f0686f0a7cf3ccefd143140924052c8462ce08c04085ca256637d54c98e3df02d68f9882d157b93b31964565fc721b511e8848275247819c18fcffb6fc6cfb56017691f4d326fbbc0d8446e06d092eb6b8e09852db682d7eaff710203010001";
//		String privateKey = "30820277020100300d06092a864886f70d0101010500048202613082025d020100028181009ed67f3ace694e4a4c1e7dbc0a005a85d775eba4b4a88b3c42086d126cc298a087c35537b79f0686f0a7cf3ccefd143140924052c8462ce08c04085ca256637d54c98e3df02d68f9882d157b93b31964565fc721b511e8848275247819c18fcffb6fc6cfb56017691f4d326fbbc0d8446e06d092eb6b8e09852db682d7eaff710203010001028181008a335b54115072bc166ad93f88fc31c70e671d769b71a263f5985b3e92bee60996d69009008d2f22adb5748d16140143b1490f0a781306ed01266918206f45f3e19ce7c96fbc2ce12a36916bb3e397966798a57a9f177093fe1a37ee4d8b7320babd2d5badc5ce3fed6f912a33c66ec4c912322a571e24d7b550512c09c4be19024100cdde25a8222f0f980adeaf76051f734bcd00197824001412186a3320e80772f5ee89509e7e9307c167e8d3713a41e079ff767c2e0a1c8fa0557db927e236f46b024100c5847e79737efd06a04c6f0b5c4794c67fce7038f0cda9b107d8f979256afca0177373096a02b14bcf101e56659f51196bb8d65a83397af58943026d45cf7293024100aad15837a15da137cc55280bea864519b15912c9103e0c69535ed35bcb8b5ac55b8f0e98e477c568a27a9aef89a25dcb7cc0689b9f0241de8cf14bf01f10f04f0240597b0eb902c3d2e93f91a0f4ea975b251a1d71e5b9e1d2306b37b6bd556044965a49692228ae93979e74d4e99b1c33e76a4fd07c2cdacb8394ed9099e478789502404b6f42b4f0b46de922ea224edb346665d7fdd231144ade894cf696199ef4e66258d61c85a775e1e3eb5946157921bf4e092a66ccd172ed3f320b4225edfda2ec";

		String str1 = "RSA加解密测试！";
		String str2 = encrypt(str1, publicKey);
		String str3 = decrypt(str2, privateKey);

		System.out.println("公钥： " + publicKey);
		System.out.println("私钥： " + privateKey);
		System.out.println("原文： " + str1);
		System.out.println("密文： " + str2);
		System.out.println("解密： " + str3);
	}
}
