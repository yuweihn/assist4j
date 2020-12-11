package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.mybatis.where.Criteria;
import com.yuweix.assist4j.dao.sharding.Sharding;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class DeleteSqlProvider extends AbstractProvider {

	public <T>String delete(final T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final String tbName = getTableName(entityClass);
		final StringBuilder tableNameBuilder = new StringBuilder(tbName);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;

			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				String shardingIndex = getShardingIndex(field.getAnnotation(Sharding.class), tbName, getFieldValue(field, t));
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
				throw new IllegalAccessException("'where' is required.");
			}
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKey(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		final StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean hasSharding = false;
			boolean whereSet = false;

			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				Sharding sharding = field.getAnnotation(Sharding.class);
				if (sharding != null) {
					hasSharding = true;
					break;
				}

				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{id}");
					whereSet = true;
				}
			}
			if (hasSharding) {
				throw new IllegalAccessException("'Sharding Value' is required.");
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKeySharding(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		final Object shardingVal = param.get("shardingVal");
		final String tbName = getTableName(entityClass);
		final StringBuilder tableNameBuilder = new StringBuilder(tbName);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;

			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				String shardingIndex = getShardingIndex(field.getAnnotation(Sharding.class), tbName, shardingVal);
				Id idAnn = field.getAnnotation(Id.class);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中
					 */
					WHERE("`" + fc.getColumnName() + "` = #{shardingVal}");
					if (idAnn != null) {
						whereSet = true;
					}
					continue;
				}

				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{id}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String deleteByCriteria(Map<String, Object> param) throws IllegalAccessException {
		Class<T> entityClass = (Class<T>) param.get("clz");
		final String tbName = getTableName(entityClass);
		final StringBuilder tableNameBuilder = new StringBuilder(tbName);
		final Criteria criteria = (Criteria) param.get("criteria");
		if (criteria == null || criteria.getParams() == null || criteria.getParams().size() <= 0) {
			throw new IllegalAccessException("'where' is required.");
		}

		final Object shardingVal = criteria.getShardingVal();
		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
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
				}
			}

			WHERE(criteria.toSql());
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}
}
