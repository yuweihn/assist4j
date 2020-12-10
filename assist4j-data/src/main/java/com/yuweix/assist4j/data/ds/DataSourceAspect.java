package com.yuweix.assist4j.data.ds;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.List;


/**
 * @author yuwei
 */
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSourceAspect {
	private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);
	private List<DataSourceCluster> dsClusterList;
	
	
	
	public DataSourceAspect(List<DataSourceCluster> dsClusterList) {
		Assert.notEmpty(dsClusterList, "[dsClusterList] is required.");
		this.dsClusterList = dsClusterList;
	}


	@Pointcut("@within(org.springframework.transaction.annotation.Transactional) "
			+ " || @annotation(org.springframework.transaction.annotation.Transactional)")
	public void cut() {

	}


	@Around("cut()")
	public Object advice(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();

		/**
		 * 1、若当前线程有数据源，直接使用当前数据源；
		 * 2、若当前线程没有数据源：
		 ****2.1、当前方法指定了数据源，就使用指定的数据源；
		 ****2.2、当前方法没有指定数据源，将默认数据库的主库指定为当前数据源。
		 */
		boolean newDataSource = !DataSourceHolder.hasDataSource();
		if (newDataSource) {
			DataSource dsAnnot = method.getAnnotation(DataSource.class);
			DataSourceCluster dsc = dsAnnot == null || DataSourceConstants.defaultDatabaseKey.equalsIgnoreCase(dsAnnot.key())
											? getDefaultCluster()
											: getDataSourceClusterByKey(dsAnnot.key());
			if (dsAnnot == null || DataSourceConstants.write.equals(dsAnnot.operation())) {
				DataSourceHolder.setDataSource(dsc.getMaster().getKey());
			} else {
				KvPair kvPair = dsc.getRouter().getTargetDataSource();
				DataSourceHolder.setDataSource(kvPair == null ? null : kvPair.getKey());
			}
			log.debug("DataSource: {}, Method: {}.", DataSourceHolder.getDataSource(), method);
		}

		Object result = point.proceed();
		if (newDataSource) {
			DataSourceHolder.clearDataSource();
		}
		return result;
	}

	private DataSourceCluster getDefaultCluster(){
		Assert.notEmpty(dsClusterList, "[dsClusterList] is required.");

		for (DataSourceCluster dsc: dsClusterList) {
			if (dsc.getIsDefault()) {
				return dsc;
			}
		}

		log.error("There isn't default database.");
		throw new RuntimeException("There isn't default database.");
	}

	private DataSourceCluster getDataSourceClusterByKey(String key) {
		Assert.notNull(key, "[key] is required.");
		Assert.notEmpty(dsClusterList, "[dsClusterList] is required.");

		for (DataSourceCluster dsc: dsClusterList) {
			if (dsc.getKey().equals(key)) {
				return dsc;
			}
		}

		log.error("There isn't database named {}.", key);
		throw new RuntimeException("There isn't database named " + key + ".");
	}
}
