package com.yuweix.assist4j.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;


/**
 * @author yuwei
 */
public class IndexModifyCallback extends IndexParamCallback {
	protected String sql;
	protected Object[] params;

	public IndexModifyCallback(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		SQLQuery query = session.createSQLQuery(sql);
		assembleParams(query, params);
		return query.executeUpdate();
	}
}
