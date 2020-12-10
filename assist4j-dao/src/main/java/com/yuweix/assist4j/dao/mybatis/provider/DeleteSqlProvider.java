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
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			DELETE_FROM(tableName);
			boolean whereSet = false;

			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKey(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
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
				throw new IllegalAccessException("'where' is required.");
			}
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String deleteByCriteria(Map<String, Object> param) throws IllegalAccessException {
		Class<T> entityClass = (Class<T>) param.get("clz");
		final String tableName = getTableName(entityClass);
		final Criteria criteria = (Criteria) param.get("criteria");
		if (criteria == null || criteria.getParams() == null || criteria.getParams().size() <= 0) {
			throw new IllegalAccessException("'where' is required.");
		}
		return new SQL() {{
			DELETE_FROM(tableName);
			WHERE(criteria.toSql());
		}}.toString();
	}
}
