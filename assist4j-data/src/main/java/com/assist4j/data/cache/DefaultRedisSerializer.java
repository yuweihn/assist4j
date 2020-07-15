package com.assist4j.data.cache;


import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.*;


/**
 * @author yuwei
 */
public class DefaultRedisSerializer implements RedisSerializer<Object> {
	@Override
	public byte[] serialize(Object o) throws SerializationException {
		if (o == null) {
			return null;
		}
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null) {
			return null;
		}
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
