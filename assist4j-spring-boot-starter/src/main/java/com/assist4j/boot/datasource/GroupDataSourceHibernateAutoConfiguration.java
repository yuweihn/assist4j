package com.assist4j.boot.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.yuweix.assist4j.data.ds.DataSourceCluster;
import com.yuweix.assist4j.data.ds.KvPair;
import com.yuweix.assist4j.data.springboot.GroupDataSourceHibernateConf;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.group.datasource.hibernate.enabled")
@Import({GroupDataSourceHibernateConf.class})
public class GroupDataSourceHibernateAutoConfiguration {

	@ConditionalOnMissingBean(name = "ds-master")
	@Bean(name = "ds-master", initMethod = "init", destroyMethod = "close")
	public DruidDataSource druidDataSourceMaster(@Value("${master.jdbc.driver.class}") String driverClassName
			, @Value("${master.jdbc.url}") String url
			, @Value("${master.jdbc.userName}") String userName
			, @Value("${master.jdbc.password}") String password
			, @Value("${master.jdbc.default.read.only:false}") boolean defaultReadOnly
			, @Value("${master.jdbc.filters:stat}") String filters
			, @Value("${master.jdbc.max.active:1}") int maxActive
			, @Value("${master.jdbc.initial.size:1}") int initialSize
			, @Value("${master.jdbc.max.wait:60000}") long maxWaitMillis
			, @Value("${master.jdbc.remove.abandoned:true}") boolean removeAbandoned
			, @Value("${master.jdbc.remove.abandoned.timeout:1800}") int removeAbandonedTimeout
			, @Value("${master.jdbc.min.idle:1}") int minIdle
			, @Value("${master.jdbc.time.between.eviction.runs.millis:60000}") long timeBetweenEvictionRunsMillis
			, @Value("${master.jdbc.min.evictable.idle.time.millis:300000}") long minEvictableIdleTimeMillis
			, @Value("${master.jdbc.validation.query:select 'x'}") String validationQuery
			, @Value("${master.jdbc.test.while.idle:true}") boolean testWhileIdle
			, @Value("${master.jdbc.test.on.borrow:false}") boolean testOnBorrow
			, @Value("${master.jdbc.test.on.return:false}") boolean testOnReturn
			, @Value("${master.jdbc.pool.prepared.statements:true}") boolean poolPreparedStatements
			, @Value("${master.jdbc.max.pool.prepared.statement.per.connection.size:50}") int maxPoolPreparedStatementPerConnectionSize
			, @Value("${master.jdbc.max.open.prepared.statements:100}") int maxOpenPreparedStatements) throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		dataSource.setDefaultReadOnly(defaultReadOnly);
		dataSource.setFilters(filters);
		dataSource.setMaxActive(maxActive);
		dataSource.setInitialSize(initialSize);
		dataSource.setMaxWait(maxWaitMillis);
		dataSource.setRemoveAbandoned(removeAbandoned);
		dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		dataSource.setMinIdle(minIdle);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		dataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
		return dataSource;
	}

	@ConditionalOnMissingBean(name = "dsClusterList")
	@Bean(name = "dsClusterList")
	public List<DataSourceCluster> dsClusterList(@Qualifier("ds-master") DataSource dataSourceMaster
			, @Value("${assist4j.ds.cluster.master.key}") String masterKey) {
		List<DataSourceCluster> dsClusterList = new ArrayList<DataSourceCluster>();
		DataSourceCluster dsCluster = new DataSourceCluster();
		dsCluster.setKey(masterKey);
		dsCluster.setIsDefault(true);
		dsCluster.setMaster(new KvPair("dsMaster", dataSourceMaster));
		dsClusterList.add(dsCluster);
		return dsClusterList;
	}
}
