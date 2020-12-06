package com.yuweix.assist4j.dao.mybatis;


import com.yuweix.assist4j.dao.mybatis.provider.InsertSqlProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;

import java.io.Serializable;
import java.util.List;


/**
 * @author yuwei
 */
public interface InsertMapper<T extends Serializable, PK extends Serializable> {
	@Options(useGeneratedKeys = true)
	@InsertProvider(type = InsertSqlProvider.class, method = "insert")
	int insert(T t);

	@Options(useGeneratedKeys = true)
	@InsertProvider(type = InsertSqlProvider.class, method = "insertSelective")
	int insertSelective(T t);

	@Options(useGeneratedKeys = true)
	@InsertProvider(type = InsertSqlProvider.class, method = "batchInsert")
	int batchInsert(List<T> list);
}
