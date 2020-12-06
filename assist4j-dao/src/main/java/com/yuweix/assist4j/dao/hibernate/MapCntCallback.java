package com.yuweix.assist4j.dao.hibernate;


import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;


/**
 * @author wei
 */
public class MapCntCallback extends MapParamCallback {
	protected String sql;
	protected Map<String, Object> params;

	public MapCntCallback(String sql, Map<String, Object> params) {
		this.sql = sql;
		this.params = params;
	}

	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		SQLQuery query = session.createSQLQuery(sql);
		assembleParams(query, params);
		return new Integer(query.uniqueResult().toString());
	}
}
