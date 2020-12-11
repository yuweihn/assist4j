package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.mybatis.where.Criteria;
import com.yuweix.assist4j.dao.sharding.Sharding;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import javax.persistence.Version;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class UpdateSqlProvider extends AbstractProvider {
	public <T>String updateByPrimaryKey(T t) throws IllegalAccessException {
		return toUpdateByPrimaryKeySql(t, false);
	}

	public <T>String updateByPrimaryKeySelective(T t) throws IllegalAccessException {
		return toUpdateByPrimaryKeySql(t, true);
	}

	private <T>String toUpdateByPrimaryKeySql(final T t, final boolean selective) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final String tbName = getTableName(entityClass);
		final StringBuilder tableNameBuilder = new StringBuilder(tbName);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				field.setAccessible(true);

				String shardingIndex = getShardingIndex(field.getAnnotation(Sharding.class), tbName, getFieldValue(field, t));
				Id idAnn = field.getAnnotation(Id.class);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中，且一定不能修改
					 */
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					if (idAnn != null) {
						whereSet = true;
					}
					continue;
				}

				if (selective) {
					Object o = field.get(t);
					if (o == null) {
						continue;
					}
				}

				Version version = field.getAnnotation(Version.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					whereSet = true;
				} else if (version != null) {
					int val = field.getInt(t);
					SET("`" + fc.getColumnName() + "`" + " = " + (val + 1));
					WHERE("`" + fc.getColumnName() + "` = " + val);
				} else {
					SET("`" + fc.getColumnName() + "`" + " = #{" + field.getName() + "} ");
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			UPDATE(tableNameBuilder.toString());
		}}.toString();
	}

	public <T>String updateByCriteria(Map<String, Object> param) throws IllegalAccessException {
		return toUpdateByCriteriaSql(param, false);
	}

	public <T>String updateByCriteriaSelective(Map<String, Object> param) throws IllegalAccessException {
		return toUpdateByCriteriaSql(param, true);
	}

	@SuppressWarnings("unchecked")
	private <T>String toUpdateByCriteriaSql(Map<String, Object> param, final boolean selective) throws IllegalAccessException {
		final T t = (T) param.get("t");
		Class<?> entityClass = t.getClass();
		final List<String> excludeFields = (List<String>) param.get("excludeFields");
		final Criteria criteria = (Criteria) param.get("criteria");
		if (criteria == null || criteria.getParams() == null || criteria.getParams().size() <= 0) {
			throw new IllegalAccessException("'where' is required.");
		}
		final String tbName = getTableName(entityClass);
		final StringBuilder tableNameBuilder = new StringBuilder(tbName);

		final Object shardingVal = criteria.getShardingVal();
		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				field.setAccessible(true);

				Sharding sharding = field.getAnnotation(Sharding.class);
				if (sharding != null) {
					if (shardingVal == null) {
						throw new IllegalAccessException("'Sharding Value' is required.");
					}
					String shardingIndex = getShardingIndex(sharding, tbName, shardingVal);
					if (shardingIndex != null) {
						tableNameBuilder.append("_").append(shardingIndex);
						/**
						 * 分片字段，必须放在where子句中
						 */
						WHERE("`" + fc.getColumnName() + "` = #{criteria.shardingVal} ");
					}
					continue;
				}

				if (excludeFields != null && excludeFields.contains(field.getName())) {
					continue;
				}

				if (selective) {
					Object o = field.get(t);
					if (o == null) {
						continue;
					}
				}

				Version version = field.getAnnotation(Version.class);
				if (version != null) {
					int val = field.getInt(t);
					SET("`" + fc.getColumnName() + "`" + " = " + (val + 1));
					WHERE("`" + fc.getColumnName() + "` = " + val);
				} else {
					SET("`" + fc.getColumnName() + "`" + " = #{t." + field.getName() + "} ");
				}
			}
			WHERE(criteria.toSql());
			UPDATE(tableNameBuilder.toString());
		}}.toString();
	}
}

