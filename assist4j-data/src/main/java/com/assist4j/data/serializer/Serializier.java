package com.assist4j.data.serializer;




/**
 * @author yuwei
 */
public interface Serializier {
	<T>String serialize(T t);
	<T>T deserialize(String str);
}
