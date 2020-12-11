package com.yuweix.assist4j.dao.sharding;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingConf {
    @ConditionalOnMissingBean(name = "shardingBeanHolder")
    @Bean(name = "shardingBeanHolder")
    @ConfigurationProperties(prefix = "assist4j", ignoreUnknownFields = true)
    public ShardingBeanHolder shardingBeanHolder() throws NoSuchFieldException, IllegalAccessException {
        ShardingBeanHolder holder = new ShardingBeanHolder() {
            private Map<String, Config> conf = new HashMap<String, Config>();

            @Override
            public Map<String, Config> getShardingConf() {
                return conf;
            }
        };

        Field field = AbstractStrategy.class.getDeclaredField(AbstractStrategy.FIELD_CONF_MAP_NAME);
        field.setAccessible(true);
        field.set(new AbstractStrategy() {
            @Override
            public String getShardingIndex(String tableName, Object shardingVal) {
                return null;
            }
        }, holder.getShardingConf());

        return holder;
    }
}
