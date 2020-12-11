package com.yuweix.assist4j.dao.sharding;



/**
 * 分片策略-取模
 * @author yuwei
 */
public class ModStrategy extends AbstractStrategy {
    @Override
    public String getShardingIndex(String tableName, Object shardingVal) {
        ShardingBeanHolder.Config conf = confMap.get(tableName);
        if (conf == null) {
            throw new RuntimeException("[" + tableName + "]'s sharding-conf is required.");
        }
        return String.format("%0" + conf.getSuffixLength() + "d", hash(shardingVal) % conf.getShardingSize());
    }

    private int hash(Object str) {
        if (str == null) {
            return 0;
        }
        int hashCode = str.hashCode();
        return hashCode;
    }
}
