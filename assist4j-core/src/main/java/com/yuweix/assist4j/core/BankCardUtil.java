package com.yuweix.assist4j.core;


import java.util.regex.Pattern;


/**
 * 银行卡工具类
 * @author yuwei
 */
public abstract class BankCardUtil {
	/**
	 * 根据Luhn算法，校验银行卡格式
	 * Luhn算法被用于最后一位为校验码的一串数字的校验，通过如下规则计算校验码的正确性：
	 * 第一步：从右往左，从这串数字的右边开始，将偶数位数字乘以2，如果每次乘2的结果大于9（如：2 * 8 = 16），
	 * 然后计算个位和十位数字的和（如 1 + 6 = 7）或者用这个结果减去9（16 - 9 = 7）；
	 * 第二步： 第一步操作过后，会得到新的一串数字，计算所有数字的和（包含校验码位）；
	 * 第三步： 用第二步得到的和进行模10运算，如果结果为0，表示校验通过，否则失败
	 * demo：
	 * 银行卡号：       6 2 2 4 7 0 2 8  1
	 * 乘以2的结果：    6 4 2 8 7 0 2 16 1
	 * 计算和的结果：   6 4 2 8 7 0 2 7  1
	 * 计算的和为37，模10的结果为7，不是0，则表示该银行卡号不正确
	 * @param bankNo
	 * @return
	 */
	public static boolean check(String bankNo) {
		// 第一步： 首先通过正则判断，是不是全由数字组成
		if (!Pattern.matches("^[0-9]{6,}$", bankNo)) {
			return false;
		}
		// 第二步： 将银行卡字符串,倒序转换为int数组：如 "478531",转换为[1,3,5,8,7,4]
		int length = bankNo.length();  // 字符串长度
		int[] accountDigits = new int[length];
		for (int i = 0; i < length; i++) {
			accountDigits[i] = Integer.parseInt(bankNo.substring(length - i - 1, length - i));
		}

		// 第三步：将第二步得到的数组，偶数位*2处理后，计算和
		int sum = 0;
		for (int i = 0; i < length; i++) {
			int digit = accountDigits[i];
			// 偶数位，做乘2处理
			if ((i + 1) % 2 == 0) {
				digit =  accountDigits[i] * 2;
				if (digit > 9) {
					digit = digit - 9;
				}
			}
			sum = sum + digit;
		}

		// 第四步：检查第三步计算的和，模10的结果是否为0
		return sum % 10 == 0;
	}
}
