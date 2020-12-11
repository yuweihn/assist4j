package com.yuweix.assist4j.dao;


import java.io.Serializable;
import java.util.Collection;
import java.util.List;


/**
 * @author yuwei
 */
public interface Dao<T extends Serializable, PK extends Serializable> {
	List<T> getAll();
	T get(PK id);
	void save(T entity);
	void saveOrUpdateAll(Collection<T> entities);
	void update(T entity);
	void saveOrUpdate(T entity);
	T merge(T entity);
	void deleteByKey(PK id);
	void delete(T entity);
}
