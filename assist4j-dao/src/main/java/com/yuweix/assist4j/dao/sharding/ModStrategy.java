package com.yuweix.assist4j.dao.sharding;



/**
 * 分片策略-对2取模
 * @author yuwei
 */
public class ModStrategy implements Strategy {
    private int shardingSize = 2;

    @Override
    public String getShardingIndex(Object val, int suffixLength) {
        return String.format("%0" + suffixLength + "d", hash(val) % shardingSize);
    }

    private int hash(Object str) {
        if (str == null) {
            return 0;
        }
        int hashCode = str.hashCode();
        return hashCode;
    }
}
