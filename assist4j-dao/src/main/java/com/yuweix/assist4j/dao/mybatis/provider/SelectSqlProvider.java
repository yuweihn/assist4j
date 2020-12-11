package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.mybatis.order.OrderBy;
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
public class SelectSqlProvider extends AbstractProvider {
	@SuppressWarnings("unchecked")
	public <PK, T>String selectOneById(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));
		
		List<FieldColumn> fcList = getPersistFieldList(entityClass);
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

				SELECT(fc.getColumnName() + " as " + field.getName());
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE(fc.getColumnName() + " = #{id}");
					whereSet = true;
				}
			}
			if (hasSharding) {
				throw new IllegalAccessException("'Sharding Value' is required.");
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <PK, T>String selectOneByIdSharding(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		Object shardingVal = param.get("shardingVal");
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				SELECT(fc.getColumnName() + " as " + field.getName());

				String shardingIndex = getShardingIndex(field.getAnnotation(Sharding.class), shardingVal);
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
					WHERE(fc.getColumnName() + " = #{id}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findCount(Map<String, Object> param) throws IllegalAccessException {
		Criteria criteria = (Criteria) param.get("criteria");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		Object shardingVal = criteria == null ? null : criteria.getShardingVal();
		List<FieldColumn> fcList = getPersistFieldList(entityClass);

		String shardingWhere = null;
		for (FieldColumn fc: fcList) {
			Field field = fc.getField();
			Sharding sharding = field.getAnnotation(Sharding.class);
			if (sharding != null) {
				if (shardingVal == null) {
					throw new IllegalAccessException("'Sharding Value' is required.");
				}
				String shardingIndex = getShardingIndex(sharding, shardingVal);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中
					 */
					shardingWhere = "`" + fc.getColumnName() + "` = #{criteria.shardingVal} ";
				}
			}
		}

		StringBuilder builder = new StringBuilder("");
		builder.append(" select count(1) as cnt ");
		builder.append(" from ").append(tableNameBuilder.toString()).append("  ");
		if (shardingWhere == null) {
			builder.append(" where 1 = 1 ");
		} else {
			builder.append(" where ").append(shardingWhere);
		}

		if (criteria != null) {
			String criteriaSql = criteria.toSql();
			if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
				builder.append(" and ").append(criteriaSql).append(" ");
			}
		}

		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findList(Map<String, Object> param) throws IllegalAccessException {
		Criteria criteria = (Criteria) param.get("criteria");
		OrderBy orderBy = (OrderBy) param.get("orderBy");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		Integer pageNo0 = null;
		if (param.containsKey("pageNo")) {
			pageNo0 = (Integer) param.get("pageNo");
		}
		Integer pageSize0 = null;
		if (param.containsKey("pageSize")) {
			pageSize0 = (Integer) param.get("pageSize");
		}
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		Object shardingVal = criteria == null ? null : criteria.getShardingVal();
		List<FieldColumn> fcList = getPersistFieldList(entityClass);

		String shardingWhere = null;
		for (FieldColumn fc: fcList) {
			Field field = fc.getField();
			Sharding sharding = field.getAnnotation(Sharding.class);
			if (sharding != null) {
				if (shardingVal == null) {
					throw new IllegalAccessException("'Sharding Value' is required.");
				}
				String shardingIndex = getShardingIndex(sharding, shardingVal);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中
					 */
					shardingWhere = "`" + fc.getColumnName() + "` = #{criteria.shardingVal} ";
				}
			}
		}

		StringBuilder builder = new StringBuilder("");
		builder.append(" select ").append(getAllColumnSql(entityClass));
		builder.append(" from ").append(tableNameBuilder.toString()).append("  ");
		if (shardingWhere == null) {
			builder.append(" where 1 = 1 ");
		} else {
			builder.append(" where ").append(shardingWhere);
		}

		if (criteria != null) {
			String criteriaSql = criteria.toSql();
			if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
				builder.append(" and ").append(criteriaSql).append(" ");
			}
		}

		if (orderBy != null) {
			String orderBySql = orderBy.toSql();
			if (orderBySql != null && !"".equals(orderBySql.trim())) {
				builder.append(" order by ").append(orderBySql.trim()).append(" ");
			}
		}

		if (pageNo0 != null && pageSize0 != null) {
			int pageNo = pageNo0 <= 0 ? 1 : pageNo0;
			int pageSize = pageSize0 <= 0 ? 10 : pageSize0;

			builder.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
		}
		return builder.toString();
	}
}

