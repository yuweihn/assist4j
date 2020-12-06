package com.yuweix.assist4j.dao.hibernate;


import org.springframework.orm.hibernate4.HibernateCallback;


/**
 * @author wei
 */
public abstract class AbstractParamCallback implements HibernateCallback<Object> {
	protected static final int DEFAULT_PAGE_SIZE = 10;
}
