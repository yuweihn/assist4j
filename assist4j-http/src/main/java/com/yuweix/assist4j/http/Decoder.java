package com.yuweix.assist4j.http;





/**
 * @author yuwei
 */
public interface Decoder<T extends Decoder<T>> {
	T decode(String str);
}
