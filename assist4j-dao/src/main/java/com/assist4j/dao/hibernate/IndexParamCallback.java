package com.assist4j.dao.hibernate;


import org.hibernate.Query;


/**
 * @author wei
 */
public abstract class IndexParamCallback extends AbstractParamCallback {
	protected void assembleParams(Query query, Object[] params) {
		if(params == null || params.length <= 0) {
			return;
		}

		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
	}
}
