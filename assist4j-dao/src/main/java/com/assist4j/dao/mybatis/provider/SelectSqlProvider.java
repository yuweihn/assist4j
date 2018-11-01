package com.assist4j.dao.mybatis.provider;


import com.assist4j.dao.mybatis.where.Criteria;
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
		String tableName = getTableName(entityClass);
		
		List<FieldColumn> fcList = getPersistFieldList(entityClass);
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
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String selectCount(Map<String, Object> param) {
		Map<String, Object> whereMap = (Map<String, Object>) param.get("where");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		String tableName = getTableName(entityClass);

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			FROM(tableName);
			SELECT("count(1) as cnt ");
			if (whereMap != null) {
				for (FieldColumn fc : fcList) {
					if (whereMap.containsKey(fc.getColumnName())) {
						WHERE(fc.getColumnName() + " = #{[where]." + fc.getColumnName() + " }");
					}
				}
			}
		}}.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <T>String selectList(Map<String, Object> param) {
		Map<String, Object> whereMap = (Map<String, Object>) param.get("where");
		String orderBy = (String) param.get("orderBy");
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

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		StringBuilder sql = new StringBuilder(new SQL() {{
			FROM(tableName);
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				SELECT(fc.getColumnName() + " as " + field.getName());

				if (whereMap != null) {
					if (whereMap.containsKey(fc.getColumnName())) {
						WHERE(fc.getColumnName() + " = #{[where]." + fc.getColumnName() + " }");
					}
				}
			}
			if (orderBy != null && !"".equals(orderBy.trim())) {
				ORDER_BY(orderBy);
			}
		}}.toString());

		if (pageNo0 != null && pageSize0 != null) {
			int pageNo = pageNo0 <= 0 ? 1 : pageNo0;
			int pageSize = pageSize0 <= 0 ? 10 : pageSize0;

			sql.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
		}
		return sql.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findCount(Map<String, Object> param) {
		Criteria criteria = (Criteria) param.get("criteria");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		String tableName = getTableName(entityClass);

		StringBuilder builder = new StringBuilder("");
		builder.append("  select count(1) as cnt ")
				.append(" from ").append(tableName).append("  ")
				.append(assembleWhere(criteria));

		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findList(Map<String, Object> param) {
		Criteria criteria = (Criteria) param.get("criteria");
		String orderBy = (String) param.get("orderBy");
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
		builder.append("  select ").append(getSelectSql(entityClass))
				.append(" from ").append(tableName).append("  ")
				.append(assembleWhere(criteria));

		if (orderBy != null && !"".equals(orderBy.trim())) {
			builder.append(" ").append(orderBy).append(" ");
		}

		if (pageNo0 != null && pageSize0 != null) {
			int pageNo = pageNo0 <= 0 ? 1 : pageNo0;
			int pageSize = pageSize0 <= 0 ? 10 : pageSize0;

			builder.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
		}
		return builder.toString();
	}

	private String assembleWhere(Criteria criteria) {
		if (criteria == null) {
			return "";
		}

		return "";
	}
}

