package com.yuweix.assist4j.data.ds;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author yuwei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
	String key() default DataSourceConstants.defaultDatabaseKey;
	String operation() default DataSourceConstants.write;
}
