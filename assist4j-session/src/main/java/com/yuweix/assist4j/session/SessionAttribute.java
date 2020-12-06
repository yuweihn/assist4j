package com.yuweix.assist4j.session;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;


/**
 * Session的相关属性键值对储存Bean.
 * @author yuwei
 */
public class SessionAttribute implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private boolean newBuild;
	private Date lastAccessTime;
	private Date createTime;
	private String repeatKey;
	private Object repeatValue;
	private Set<AttributeData> attributes;


	public boolean isEmpty() {
		return attributes == null || attributes.size() <= 0;
	}

	public SessionAttribute() {
		attributes = new HashSet<AttributeData>();
	}
	public SessionAttribute(Date createTime) {
		this();
		this.createTime = createTime;
	}

	public void putAttribute(String name, Object value) {
		if (name == null || "".equals(name) || value == null || "".equals(value)) {
			return;
		}
		AttributeData data = new AttributeData();
		data.setKey(name);
		data.setValue(value);
		data.setValueClassName(value.getClass().getName());
		attributes.remove(data);
		attributes.add(data);
	}

	public Object removeAttribute(String name) {
		if (name == null || isEmpty()) {
			return null;
		}
		for (AttributeData data: attributes) {
			if (name.equals(data.getKey())) {
				Object value = data.getValue();
				attributes.remove(data);
				return value;
			}
		}
		return null;
	}

	public Object getAttribute(String name) {
		if (name == null || isEmpty()) {
			return null;
		}
		for (AttributeData data: attributes) {
			if (name.equals(data.getKey())) {
				return data.getValue();
			}
		}
		return null;
	}

	public Set<String> getAttributeNames() {
		Set<String> set = new HashSet<String>();
		if (isEmpty()) {
			return set;
		}

		for (AttributeData data: attributes) {
			set.add(data.getKey());
		}
		return set;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public boolean isNewBuild() {
		return newBuild;
	}

	public void setNewBuild(boolean newBuild) {
		this.newBuild = newBuild;
	}

	public String getRepeatKey() {
		return repeatKey;
	}

	public void setRepeatKey(String repeatKey) {
		this.repeatKey = repeatKey;
	}

	public Object getRepeatValue() {
		return repeatValue;
	}

	public void setRepeatValue(Object repeatValue) {
		this.repeatValue = repeatValue;
	}


	public static String encode(SessionAttribute attr) {
		if (attr == null) {
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("newBuild", attr.newBuild);
		map.put("lastAccessTime", attr.lastAccessTime == null ? null : formatDate(attr.lastAccessTime, "yyyy-MM-dd HH:mm:ss.SSS"));
		map.put("createTime", attr.createTime == null ? null : formatDate(attr.createTime, "yyyy-MM-dd HH:mm:ss.SSS"));

		if (!attr.isEmpty()) {
			List<String> attributeStrList = new ArrayList<String>();
			for (AttributeData data: attr.attributes) {
				attributeStrList.add(data.encode());
			}
			map.put("attribute", attributeStrList);
		}
		if (attr.repeatKey != null && !"".equals(attr.repeatKey) && attr.repeatValue != null && !"".equals(attr.repeatValue.toString())) {
			map.put("repeatKey", attr.repeatKey);
			map.put("repeatValue", attr.repeatValue);
		}
		return JSONObject.toJSONString(map);
	}

	@SuppressWarnings("unchecked")
	public static SessionAttribute decode(String value) {
		Map<String, Object> map = null;
		try {
			map = JSONObject.parseObject(value, Map.class);
		} catch (Exception e) {
		}
		
		if (map == null) {
			return null;
		}

		Object newBuildObj = map.get("newBuild");
		Object lastAccessTimeObj = map.get("lastAccessTime");
		Object createTimeObj = map.get("createTime");
		Object attributeObj = map.get("attribute");
		Object repeatKeyObj = map.get("repeatKey");
		Object repeatValueObj = map.get("repeatValue");
		
		SessionAttribute attr = new SessionAttribute();
		attr.newBuild = Boolean.valueOf(newBuildObj == null ? "false" : newBuildObj.toString());
		attr.lastAccessTime = lastAccessTimeObj == null ? null 
						: parseDate(lastAccessTimeObj.toString(), "yyyy-MM-dd HH:mm:ss.SSS");
		attr.createTime = createTimeObj == null ? null 
						: parseDate(createTimeObj.toString(), "yyyy-MM-dd HH:mm:ss.SSS");

		List<String> attributeStrList = (List<String>) attributeObj;
		if (attributeStrList != null && attributeStrList.size() > 0) {
			Set<AttributeData> _attributes = new HashSet<AttributeData>();
			for (String attributeStr: attributeStrList) {
				AttributeData data = new AttributeData();
				data.decode(attributeStr);
				_attributes.add(data);
			}
			attr.attributes = _attributes;
		}
		attr.repeatKey = (String) repeatKeyObj;
		attr.repeatValue = repeatValueObj;
		return attr;
	}
	
	/**
	 * 按指定格式格式化日期
	 * @param date
	 * @param pattern
	 * @return
	 */
	private static String formatDate(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}
	
	/**
	 * 按指定格式解析日期
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	private static Date parseDate(String dateStr, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
