package com.yuweix.assist4j.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * 分片策略
 * @author yuwei
 */
public interface Strategy {
    Map<String, Config> confMap = new HashMap<>();

    /**
     * @param shardingVal                分片字段的值
     * @return   返回分片。如：0000,0001等等
     */
    String getShardingIndex(String tableName, Object shardingVal);
}
