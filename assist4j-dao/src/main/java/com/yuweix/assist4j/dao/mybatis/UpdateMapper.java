package com.yuweix.assist4j.dao.mybatis;


import java.io.Serializable;
import java.util.List;

import com.yuweix.assist4j.dao.mybatis.provider.UpdateSqlProvider;
import com.yuweix.assist4j.dao.mybatis.where.Criteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;


/**
 * @author yuwei
 */
public interface UpdateMapper<T extends Serializable, PK extends Serializable> {
    @UpdateProvider(type = UpdateSqlProvider.class, method = "updateByPrimaryKey")
    int updateByPrimaryKey(T t);

    @UpdateProvider(type = UpdateSqlProvider.class, method = "updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(T t);

    @UpdateProvider(type = UpdateSqlProvider.class, method = "updateByCriteria")
    int updateByCriteria(@Param("t") T t, @Param("excludeFields") List<String> excludeFields, @Param("criteria") Criteria criteria);

    @UpdateProvider(type = UpdateSqlProvider.class, method = "updateByCriteriaSelective")
    int updateByCriteriaSelective(@Param("t") T t, @Param("excludeFields") List<String> excludeFields, @Param("criteria") Criteria criteria);
}
