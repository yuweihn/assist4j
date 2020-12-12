package com.yuweix.assist4j.dao.sharding;


import java.util.Map;


/**
 * @author yuwei
 **/
public interface ShardingTableHolder {
    Map<String, Config> getShardingConf();
}
