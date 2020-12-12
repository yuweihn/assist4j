package com.yuweix.assist4j.dao.sharding;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingConf {
    @ConditionalOnMissingBean(name = "shardingBeanHolder")
    @Bean(name = "shardingBeanHolder")
    @ConfigurationProperties(prefix = "assist4j", ignoreUnknownFields = true)
    public ShardingBeanHolder shardingBeanHolder() {
        return new ShardingBeanHolder() {
            private Map<String, Config> conf = new HashMap<String, Config>();

            @Override
            public Map<String, Config> getShardingConf() {
                return conf;
            }
        };
    }

    @Bean(name = "ShardingConf$tableConf")
    public Map<String, Config> initTableConfMap(@Qualifier("shardingBeanHolder") ShardingBeanHolder shardingBeanHolder) {
        Map<String, Config> conf = shardingBeanHolder.getShardingConf();

        Strategy.confMap.clear();
        if (conf != null) {
            Strategy.confMap.putAll(conf);
        }
        return conf;
    }
}
