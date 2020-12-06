package com.yuweix.assist4j.session;


import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.util.StringUtils;


/**
 * @author yuwei
 */
public class AttributeData {
	private String key;
	private Object value;
	private String valueClassName;


	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getValueClassName() {
		return valueClassName;
	}
	public void setValueClassName(String valueClassName) {
		this.valueClassName = valueClassName;
	}



	@Override
	public int hashCode() {
		return key == null ? 0 : key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AttributeData)) {
			return false;
		}

		AttributeData ad = (AttributeData) obj;
		if (StringUtils.isEmpty(ad.getKey()) && StringUtils.isEmpty(this.getKey())) {
			return true;
		}
		if (!StringUtils.isEmpty(ad.getKey()) && !StringUtils.isEmpty(this.getKey())) {
			return ad.getKey().equals(this.getKey());
		}
		return false;
	}


	public String encode() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", key);
		map.put("value", JSONObject.toJSONString(value, SerializerFeature.WriteClassName));
		map.put("valueClassName", valueClassName);

		ParserConfig.getGlobalInstance().addAccept(valueClassName);
		ParserConfig.getGlobalInstance().addAccept(this.getClass().getName());
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		return JSONObject.toJSONString(map, SerializerFeature.WriteClassName);
	}
	@SuppressWarnings("unchecked")
	public void decode(String str) {
		Map<String, String> map = JSONObject.parseObject(str, Map.class);
		String _key = map.get("key");
		String _value = map.get("value");
		String _valueClassName = map.get("valueClassName");

		this.key = _key;
		this.valueClassName = _valueClassName;

		ParserConfig.getGlobalInstance().addAccept(this.valueClassName);
		ParserConfig.getGlobalInstance().addAccept(this.getClass().getName());
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);

		try {
			Class<?> vClz = Class.forName(_valueClassName);
			this.value = JSONObject.parseObject(_value, vClz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
