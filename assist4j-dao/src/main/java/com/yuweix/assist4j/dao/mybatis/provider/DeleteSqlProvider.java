package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.mybatis.where.Criteria;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class DeleteSqlProvider extends AbstractProvider {

	public <T>String delete(T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;

			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				String shardingIndex = getShardingIndex(field, t);
				Id idAnn = field.getAnnotation(Id.class);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中
					 */
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					if (idAnn != null) {
						whereSet = true;
					}
					continue;
				}

				/**
				 * ID字段，必须放在where子句中
				 */
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
			INSERT_INTO(tableNameBuilder.toString());
		}}.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKey(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		String tableName = getTableName(entityClass);

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			DELETE_FROM(tableName);
			boolean whereSet = false;

			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{id}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String deleteByCriteria(Map<String, Object> param) throws IllegalAccessException {
		Class<T> entityClass = (Class<T>) param.get("clz");
		String tableName = getTableName(entityClass);
		Criteria criteria = (Criteria) param.get("criteria");
		if (criteria == null || criteria.getParams() == null || criteria.getParams().size() <= 0) {
			throw new IllegalAccessException("'where' is missed.");
		}
		return new SQL() {{
			DELETE_FROM(tableName);
			WHERE(criteria.toSql());
		}}.toString();
	}
}