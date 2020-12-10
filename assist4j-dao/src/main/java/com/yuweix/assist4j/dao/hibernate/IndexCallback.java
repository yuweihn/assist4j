package com.yuweix.assist4j.dao.hibernate;


import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;


/**
 * @author yuwei
 */
public class IndexCallback extends IndexParamCallback {
	protected String sql;
	protected Class<?> clz;
	protected Object[] params;
	protected Integer pageNo;
	protected Integer pageSize;

	public IndexCallback(String sql, Class<?> clz, Object[] params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
	}

	public IndexCallback(String sql, Class<?> clz, int pageNo, int pageSize, Object[] params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		SQLQuery query = session.createSQLQuery(sql);
		assembleParams(query, params);

		if (clz == null) {
			
		} else if (Number.class.isAssignableFrom(clz)) {

		} else if (Map.class.isAssignableFrom(clz)) {
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		} else {
			query.addEntity(clz);
		}

		if (pageNo != null && pageSize != null) {
			if (pageNo <= 0) {
				pageNo = 1;
			}
			if (pageSize <= 0) {
				pageSize = DEFAULT_PAGE_SIZE;
			}
			query.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize);
		}

		return query.list();
	}
}
