package com.yuweix.assist4j.data.springboot;


import com.yuweix.assist4j.data.ds.DataSourceCluster;
import com.yuweix.assist4j.data.ds.DataSourceAspect;
import com.yuweix.assist4j.data.ds.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;


/**
 * @author yuwei
 */
@EnableTransactionManagement(proxyTargetClass = true)
public class GroupDataSourceMybatisConf {
	@ConditionalOnMissingBean(name = "mapperLocations")
	@Bean(name = "mapperLocations")
	public Resource[] mapperLocations(@Value("${assist4j.mybatis.mapper.locationPattern:}") String locationPattern) throws IOException {
		if (locationPattern == null || "".equals(locationPattern)) {
			return new Resource[0];
		}
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources(locationPattern);
		return resources;
	}

	@ConditionalOnMissingBean(name = "basePackage")
	@Bean(name = "basePackage")
	public String basePackage(@Value("${assist4j.mybatis.basePackage:}") String basePackage) {
		return basePackage;
	}

	@Bean(name = "dataSource")
	public DataSource dynamicDataSource(@Qualifier("dsClusterList") List<DataSourceCluster> dsClusterList) {
		return new DynamicDataSource(dsClusterList);
	}

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource
			, @Qualifier("mapperLocations") Resource[] mapperLocations) {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		if (mapperLocations != null && mapperLocations.length > 0) {
			sessionFactoryBean.setMapperLocations(mapperLocations);
		}
		return sessionFactoryBean;
	}

	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate SqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@ConditionalOnMissingBean
	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer(@Qualifier("basePackage") String basePackage) {
		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
//		configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
		configurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
		configurer.setBasePackage(basePackage);
		return configurer;
	}

	@Bean(name = "transactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	@Bean(name = "transactionTemplate")
	public TransactionTemplate transactionTemplate(@Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}

	@Bean(name = "dataSourceAspect")
	public DataSourceAspect dataSourceAdvice(@Qualifier("dsClusterList") List<DataSourceCluster> dsClusterList) {
		return new DataSourceAspect(dsClusterList);
	}
}
