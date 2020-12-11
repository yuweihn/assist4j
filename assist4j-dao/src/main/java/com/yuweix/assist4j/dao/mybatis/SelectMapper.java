package com.yuweix.assist4j.dao.mybatis;


import com.yuweix.assist4j.dao.mybatis.order.OrderBy;
import com.yuweix.assist4j.dao.mybatis.provider.SelectSqlProvider;
import com.yuweix.assist4j.dao.mybatis.where.Criteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.io.Serializable;
import java.util.List;


/**
 * @author yuwei
 */
public interface SelectMapper<T extends Serializable, PK extends Serializable> {
	@SelectProvider(type = SelectSqlProvider.class, method = "selectOneById")
	T selectOneById(@Param("id") PK id, @Param("clz") Class<T> clz);

	@SelectProvider(type = SelectSqlProvider.class, method = "selectOneByIdSharding")
	T selectOneByIdSharding(@Param("id") PK id, @Param("shardingVal") Object shardingVal, @Param("clz") Class<T> clz);

	@SelectProvider(type = SelectSqlProvider.class, method = "findCount")
	int findCount(@Param("criteria") Criteria criteria, @Param("clazz") Class<T> clazz);

	@SelectProvider(type = SelectSqlProvider.class, method = "findList")
	List<T> findList(@Param("criteria") Criteria criteria, @Param("orderBy") OrderBy orderBy, @Param("clazz") Class<T> clazz);

	@SelectProvider(type = SelectSqlProvider.class, method = "findList")
	List<T> findPageList(@Param("criteria") Criteria criteria, @Param("pageNo") int pageNo
            , @Param("pageSize") int pageSize, @Param("orderBy") OrderBy orderBy, @Param("clazz") Class<T> clazz);
}

