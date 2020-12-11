package com.yuweix.assist4j.dao.sharding;



/**
 * 分片策略-取模
 * @author yuwei
 */
public class ModStrategy implements Strategy {
    public String getShardingIndex(Object val, int suffixLength, int shardingSize) {
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
