package com.assist4j.data.cache;


import com.assist4j.data.SerializeUtil;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public abstract class AbstractCache implements Cache {
	protected String serialize(Object o) {
		return SerializeUtil.serialize(o);
	}
	protected <T>T deserialize(String str) {
		return SerializeUtil.deserialize(str);
	}

	@Override
	public boolean putSplit(String key, String value, long timeout, int maxLength) {
		if (value == null) {
			return false;
		}
		int oldSize = parseValueSize((String) get(key));
		List<String> valList = split(value, maxLength);
		int newSize = valList.size();
		boolean b = put(key, "" + newSize, timeout);
		for (int i = 0; i < newSize; i++) {
			b &= put(key + "." + i, valList.get(i), timeout + 60);
		}

		for (int i = newSize; i < oldSize; i++) {
			remove(key + "." + i);
		}
		return b;
	}

	@Override
	public String getSplit(String key) {
		int size = parseValueSize((String) get(key));
		if (size <= 0) {
			remove(key);
			return null;
		}

		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < size; i++) {
			String subVal = get(key + "." + i);
			builder.append(subVal);
		}
		return builder.toString();
	}

	@Override
	public void removeSplit(String key) {
		int size = parseValueSize((String) get(key));
		if (size <= 0) {
			remove(key);
			return;
		}

		remove(key);
		for (int i = 0; i < size; i++) {
			remove(key + "." + i);
		}
	}

	private static int parseValueSize(String val) {
		if (val == null) {
			return 0;
		}

		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			return 0;
		}
	}

	private static List<String> split(String value, int maxLength) {
		List<String> list = new ArrayList<String>();
		if (maxLength <= 0 || value.length() <= maxLength) {
			list.add(value);
			return list;
		}

		StringBuilder builder = new StringBuilder(value);
		while (builder.length() > maxLength) {
			list.add(builder.substring(0, maxLength));
			builder.delete(0, maxLength);
		}
		if (builder.length() > 0) {
			list.add(builder.toString());
		}
		return list;
	}
}
