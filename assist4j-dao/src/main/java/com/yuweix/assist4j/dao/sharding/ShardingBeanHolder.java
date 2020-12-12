package com.yuweix.assist4j.dao.sharding;


import java.util.Map;


/**
 * @author yuwei
 **/
public interface ShardingBeanHolder {
    Map<String, Config> getShardingConf();
}
