package com.yuweix.assist4j.boot.dao;


import com.yuweix.assist4j.dao.sharding.ShardingConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.sharding.enabled")
@Import({ShardingConf.class})
public class ShardingAutoConfiguration {

}
