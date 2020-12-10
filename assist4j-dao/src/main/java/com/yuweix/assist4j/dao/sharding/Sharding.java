package com.yuweix.assist4j.dao.sharding;


import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author yuwei
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Sharding {
    /**
     * 分片策略
     * @return
     */
    Class<?> strategy() default ModStrategy.class;

    /**
     * 逻辑表后占位符长度
     * eg.
     * user  ====>>>>>  user_0000
     * @return
     */
    int suffixLength() default 4;
}
