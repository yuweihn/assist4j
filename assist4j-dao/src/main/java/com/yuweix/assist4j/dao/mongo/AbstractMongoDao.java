package com.yuweix.assist4j.dao.mongo;


import com.yuweix.assist4j.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;


/**
 * @author yuwei
 */
public abstract class AbstractMongoDao<T extends Serializable, PK extends Serializable> implements Dao<T, PK> {
	private Class<T> entityClass;

	@Autowired(required = false)
	protected MongoTemplate mongoTemplate;



	@SuppressWarnings("unchecked")
	public AbstractMongoDao(){
		this.entityClass = null;
		Class<T> c = (Class<T>) super.getClass();
		Type t = c.getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			this.entityClass = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
		}
	}



	@Override
	public List<T> getAll() {
		return mongoTemplate.findAll(entityClass);
	}

	@Override
	public T get(PK id) {
		return mongoTemplate.findById(id, entityClass);
	}

	@Override
	public void save(T entity) {
		mongoTemplate.save(entity);
	}

	@Override
	public void saveOrUpdateAll(Collection<T> entities) {
		if (entities == null || entities.size() <= 0) {
			return;
		}

		for (T t: entities) {
			save(t);
		}
	}

	@Override
	public void update(T entity) {
		save(entity);
	}

	@Override
	public void saveOrUpdate(T entity) {
		save(entity);
	}

	@Override
	public T merge(T entity) {
		update(entity);
		return entity;
	}

	@Override
	public void deleteByKey(PK id) {
		T t = get(id);
		if (t != null) {
			delete(t);
		}
	}

	@Override
	public void delete(T entity) {
		mongoTemplate.remove(entity);
	}
}
