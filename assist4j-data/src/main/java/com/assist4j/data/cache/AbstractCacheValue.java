package com.assist4j.data.cache;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;


/**
 * @author yuwei
 */
public abstract class AbstractCacheValue<T extends AbstractCacheValue<T>> implements CacheValue<T> {
	@Override
	public String encode() {
		return JSONObject.toJSONString(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T decode(String value) {
		Object bean = JSONObject.parseObject(value, this.getClass());
		try {
			BeanUtils.copyProperties(bean, this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return (T) this;
	}
}