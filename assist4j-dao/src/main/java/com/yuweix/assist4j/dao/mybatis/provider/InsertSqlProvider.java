package com.yuweix.assist4j.dao.mybatis.provider;


import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class InsertSqlProvider extends AbstractProvider {
	public <T>String insert(T t) {
		Class<?> entityClass = t.getClass();
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			INSERT_INTO(tableName);
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				
				/**
				 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
				 */
				Id idAnn = field.getAnnotation(Id.class);
				GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
				if (idAnn != null && genAnn != null && genAnn.strategy() == GenerationType.IDENTITY) {
					continue;
				}
				
				VALUES("`" + fc.getColumnName() + "`", "#{" + field.getName() + "}");
			}
		}}.toString();
	}
	
	public <T>String insertSelective(final T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			INSERT_INTO(tableName);
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				field.setAccessible(true);
				Object o = field.get(t);
				if (o == null) {
					continue;
				}
				
				/**
				 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
				 */
				Id idAnn = field.getAnnotation(Id.class);
				GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
				if (idAnn != null && genAnn != null && genAnn.strategy() == GenerationType.IDENTITY) {
					continue;
				}
				
				VALUES("`" + fc.getColumnName() + "`", "#{" + field.getName() + "}");
			}
		}}.toString();
	}
	
	public <T>String batchInsert(Map<String, List<T>> param) {
		List<T> list = param.get("param1");
		T t = list.get(0);
		Class<?> entityClass = t.getClass();
		String tableName = getTableName(entityClass);

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		List<Field> persistFieldList = new ArrayList<Field>();
		for (FieldColumn fc: fcList) {
			Field field = fc.getField();
			
			/**
			 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
			 */
			Id idAnn = field.getAnnotation(Id.class);
			GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
			if (idAnn != null && genAnn != null && genAnn.strategy() == GenerationType.IDENTITY) {
				continue;
			}
			
			persistFieldList.add(field);
		}
		
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append(tableName).append(" (");
		for (int i = 0; i < persistFieldList.size(); i++) {
			Field field = persistFieldList.get(i);
			Column col = field.getAnnotation(Column.class);
			if (i == persistFieldList.size() - 1) {
				builder.append("`").append(col.name().trim()).append("`");
			} else {
				builder.append("`").append(col.name().trim()).append("`").append(", ");
			}
		}
		builder.append(" ) values ");
		for (int j = 0; j < list.size(); j++) {
			builder.append("(");
			for (int i = 0; i < persistFieldList.size(); i++) {
				Field field = persistFieldList.get(i);
				if (i == persistFieldList.size() - 1) {
					builder.append("#{").append("list[" + j + "]." + field.getName()).append("} ");
				} else {
					builder.append("#{").append("list[" + j + "]." + field.getName()).append("}, ");
				}
			}
			if (j == list.size() - 1) {
				builder.append(") ");
			} else {
				builder.append("), ");
			}
		}
		return builder.toString();
	}
}
