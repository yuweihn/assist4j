package com.assist4j.data.cache;


import java.util.Date;
import java.util.Set;


/**
 * 缓存引擎接口
 * @author yuwei
 */
public interface Cache {
	/**
	 * 是否包含指定的key
	 * 找到返回为true，找不到返回为false
	 * @param key 查找的值
	 * @return 是否找到
	 */
	boolean contains(String key);

	/**
	 * 更新指定key的值
	 * @param key 缓存key。
	 * @param value 缓存的值。
	 * @param expiredTime 过期时间(s)。
	 * @return true更新成功，false更新失败。
	 */
	<T>boolean put(String key, T value, long expiredTime);

	/**
	 * 更新指定key的值，指定过期时间。
	 * @param key 缓存key。
	 * @param value 缓存的值。
	 * @param expiredTime 过期时间。
	 * @return true更新成功，false更新失败。
	 */
	<T>boolean put(String key, T value, Date expiredTime);

	/**
	 * 获取指定key的值
	 * @param key 缓存对象的key
	 * @return 查询到的缓存的对象
	 */
	<T>T get(String key);

	/**
	 * 删除指定key
	 * @param key 缓存对象的key
	 */
	void remove(String key);

	/**
	 * 将哈希表 key 中的字段 field 的值设为 value
	 * @param key
	 * @param field
	 * @param value
	 * @param expiredTime key的过期时间(s)。
	 * @param <T>
	 */
	<T>void hset(String key, String field, T value, long expiredTime);
	Set<String> hfields(String key);
	<T>T hget(String key, String field);
	void hdel(String key, String field);
}
