package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.mybatis.order.OrderBy;
import com.yuweix.assist4j.dao.mybatis.where.Criteria;
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
		final String tableName = getTableName(entityClass);
		
		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				SELECT(fc.getColumnName() + " as " + field.getName());
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE(fc.getColumnName() + " = #{id}");
					whereSet = true;
				}
			}
			FROM(tableName);
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findCount(Map<String, Object> param) {
		Criteria criteria = (Criteria) param.get("criteria");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		String tableName = getTableName(entityClass);

		StringBuilder builder = new StringBuilder("");
		builder.append("  select count(1) as cnt ")
				.append(" from ").append(tableName).append("  ");
		if (criteria != null) {
			String criteriaSql = criteria.toSql();
			if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
				builder.append(" where ").append(criteriaSql).append(" ");
			}
		}

		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findList(Map<String, Object> param) {
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
		String tableName = getTableName(entityClass);

		StringBuilder builder = new StringBuilder("");
		builder.append("  select ").append(getAllColumnSql(entityClass))
				.append(" from ").append(tableName).append("  ");
		if (criteria != null) {
			String criteriaSql = criteria.toSql();
			if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
				builder.append(" where ").append(criteriaSql).append(" ");
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

