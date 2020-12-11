package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.sharding.Sharding;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.lang.reflect.Field;
import java.util.List;


/**
 * @author yuwei
 */
public class InsertSqlProvider extends AbstractProvider {
	public <T>String insert(T t) throws IllegalAccessException {
		return toInsertSql(t, false);
	}
	
	public <T>String insertSelective(T t) throws IllegalAccessException {
		return toInsertSql(t, true);
	}

	private <T>String toInsertSql(final T t, final boolean selective) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				field.setAccessible(true);

				String shardingIndex = getShardingIndex(field.getAnnotation(Sharding.class), getFieldValue(field, t));
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
				}

				if (selective) {
					Object o = field.get(t);
					if (o == null) {
						continue;
					}
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
			INSERT_INTO(tableNameBuilder.toString());
		}}.toString();
	}
}
