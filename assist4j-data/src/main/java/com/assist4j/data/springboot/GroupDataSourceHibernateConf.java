package com.assist4j.data.springboot;


import com.assist4j.data.ds.DataSourceCluster;
import org.hibernate.SessionFactory;
import com.assist4j.data.ds.DataSourceAspect;
import com.assist4j.data.ds.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;


/**
 * @author yuwei
 */
@EnableTransactionManagement(proxyTargetClass = true)
public class GroupDataSourceHibernateConf {

	@ConditionalOnMissingBean(name = "mappingLocations")
	@Bean(name = "mappingLocations")
	public Resource[] mappingLocations() {
		return new Resource[] {};
	}

	@Bean(name = "dataSource")
	public DataSource dynamicDataSource(@Qualifier("dsClusterList") List<DataSourceCluster> dsClusterList) {
		return new DynamicDataSource(dsClusterList);
	}

	@Bean(name = "sessionFactory")
	public LocalSessionFactoryBean localSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource
			, @Qualifier("mappingLocations") Resource[] mappingLocations
			, @Qualifier("packagesToScan") String[] packagesToScan) {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);

		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		hibernateProperties.setProperty("hibernate.current_session_context_class", "org.springframework.orm.hibernate5.SpringSessionContext");
		hibernateProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
		hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
		hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "false");
		hibernateProperties.setProperty("hibernate.show_sql", "false");
		hibernateProperties.setProperty("hibernate.jdbc.batch_size", "20");
		hibernateProperties.setProperty("hibernate.connection.release_mode", "auto");
		sessionFactoryBean.setHibernateProperties(hibernateProperties);
		sessionFactoryBean.setMappingLocations(mappingLocations);
		sessionFactoryBean.setPackagesToScan(packagesToScan);
		return sessionFactoryBean;
	}

	@Bean(name = "transactionManager")
	public HibernateTransactionManager transactionManager(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory);
		return transactionManager;
	}

	@Bean(name = "dataSourceAspect")
	public DataSourceAspect dataSourceAdvice(@Qualifier("dsClusterList") List<DataSourceCluster> dsClusterList) {
		return new DataSourceAspect(dsClusterList);
	}
}
