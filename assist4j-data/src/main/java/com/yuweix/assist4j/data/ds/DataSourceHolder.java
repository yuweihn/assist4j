package com.yuweix.assist4j.data.ds;



/**
 * @author wei
 */
public class DataSourceHolder {
	private static final ThreadLocal<String> dataSources = new ThreadLocal<String>();

	private DataSourceHolder() {

	}


	/**
	 * 设置数据源
	 **/
	public static void setDataSource(String ds) {
		dataSources.set(ds);
	}

	/**
	 * 判断当前线程是否有数据源
	 */
	public static boolean hasDataSource() {
		return dataSources.get() != null;
	}

	/**
	 * 获取数据源
	 **/
	public static String getDataSource() {
		return dataSources.get();
	}

	/**
	 * 清除数据源
	 **/
	public static void clearDataSource() {
		dataSources.remove();
	}
}