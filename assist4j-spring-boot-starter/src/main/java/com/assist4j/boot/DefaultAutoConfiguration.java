package com.assist4j.boot;


import com.yuweix.assist4j.core.SpringContext;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;


/**
 * @author yuwei
 */
@Configuration
@AutoConfigureOrder(Integer.MIN_VALUE)
@ConditionalOnProperty(name = "assist4j.boot.default.enabled", matchIfMissing = true)
public class DefaultAutoConfiguration {

	@ConditionalOnMissingBean(name = "springContext")
	@Bean(name = "springContext")
	public SpringContext springContext() {
		try {
			Class<?> clz = Class.forName(SpringContext.class.getName());
			Constructor<?> constructor = clz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return (SpringContext) constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
