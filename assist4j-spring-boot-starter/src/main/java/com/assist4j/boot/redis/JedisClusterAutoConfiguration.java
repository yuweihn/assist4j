package com.assist4j.boot.redis;


import com.yuweix.assist4j.data.springboot.jedis.JedisClusterConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.jedis.cluster.enabled")
@Import({JedisClusterConf.class})
public class JedisClusterAutoConfiguration {

}
