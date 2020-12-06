package com.yuweix.assist4j.boot.mongo;


import com.yuweix.assist4j.data.springboot.MongoConf;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.mongo.enabled")
@Import({MongoConf.class})
public class MongoAutoConfiguration {

}
