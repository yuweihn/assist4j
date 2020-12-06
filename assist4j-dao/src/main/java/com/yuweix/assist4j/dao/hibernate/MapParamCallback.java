package com.yuweix.assist4j.dao.hibernate;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Query;


/**
 * @author wei
 */
public abstract class MapParamCallback extends AbstractParamCallback {
	protected void assembleParams(Query query, Map<String, Object> params) {
		if (params == null || params.size() <= 0) {
			return;
		}

		Iterator<String> itr = params.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			Object value = params.get(key);
			
			if (value instanceof Collection<?>) {
				query.setParameterList(key, (Collection<?>)value);
			} else {
				query.setParameter(key, value);
			}
		}
	}
}
