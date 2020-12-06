package com.yuweix.assist4j.dao.mybatis;


import com.yuweix.assist4j.dao.mybatis.order.OrderBy;
import com.yuweix.assist4j.dao.mybatis.provider.SelectSqlProvider;
import com.yuweix.assist4j.dao.mybatis.where.Criteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public interface SelectMapper<T extends Serializable, PK extends Serializable> {
	@SelectProvider(type = SelectSqlProvider.class, method = "selectOneById")
	T selectOneById(@Param("id") PK id, @Param("clz") Class<T> clz);
	
	/**
	 * @param where                   where中的key为表的字段，不是对象的属性
	 * @param clazz
	 * @return
	 */
	@SelectProvider(type = SelectSqlProvider.class, method = "selectCount")
	int selectCount(@Param("where") Map<String, Object> where, @Param("clazz") Class<T> clazz);

	/**
	 * @param where                   where中的key为表的字段，不是对象的属性
	 * @param orderBy
	 * @param clazz
	 * @return
	 */
	@SelectProvider(type = SelectSqlProvider.class, method = "selectList")
	List<T> selectList(@Param("where") Map<String, Object> where, @Param("orderBy") OrderBy orderBy, @Param("clazz") Class<T> clazz);

	@SelectProvider(type = SelectSqlProvider.class, method = "selectList")
	List<T> selectPageList(@Param("where") Map<String, Object> where, @Param("pageNo") int pageNo
			, @Param("pageSize") int pageSize, @Param("orderBy") OrderBy orderBy, @Param("clazz") Class<T> clazz);



	@SelectProvider(type = SelectSqlProvider.class, method = "findCount")
	int findCount(@Param("criteria") Criteria criteria, @Param("clazz") Class<T> clazz);

	@SelectProvider(type = SelectSqlProvider.class, method = "findList")
	List<T> findList(@Param("criteria") Criteria criteria, @Param("orderBy") OrderBy orderBy, @Param("clazz") Class<T> clazz);

	@SelectProvider(type = SelectSqlProvider.class, method = "findList")
	List<T> findPageList(@Param("criteria") Criteria criteria, @Param("pageNo") int pageNo
			, @Param("pageSize") int pageSize, @Param("orderBy") OrderBy orderBy, @Param("clazz") Class<T> clazz);
}

