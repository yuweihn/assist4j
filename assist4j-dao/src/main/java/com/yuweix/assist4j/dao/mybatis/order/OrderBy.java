package com.yuweix.assist4j.dao.mybatis.order;


import java.io.Serializable;
import java.util.*;


/**
 * @author yuwei
 */
public class OrderBy implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<String> colSqlList;


	private OrderBy() {
		colSqlList = new ArrayList<String>();
	}

	public static OrderBy create(String column) {
		return new OrderBy().add(column);
	}
	public static OrderBy create(String column, Ordered ordered) {
		return new OrderBy().add(column, ordered);
	}

	public OrderBy add(String column) {
		return add(column, null);
	}
	public OrderBy add(String column, Ordered ordered) {
		colSqlList.add(createColumnOrderedSql(column, ordered));
		return this;
	}

	private String createColumnOrderedSql(String column, Ordered ordered) {
		return column + " " + (ordered == null ? "" : ordered.getCode());
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String toSql() {
		if (colSqlList == null || colSqlList.size() <= 0) {
			return null;
		}

		StringBuilder builder = new StringBuilder("");
		for (int i = 0, size = colSqlList.size(); i < size; i++) {
			String colSql = colSqlList.get(i);
			if (i > 0) {
				builder.append(", ");
			}

			builder.append(colSql);
		}
		return builder.toString();
	}
}
