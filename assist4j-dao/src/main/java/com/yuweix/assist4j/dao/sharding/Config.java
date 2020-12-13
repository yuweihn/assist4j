package com.yuweix.assist4j.dao.sharding;


/**
 * @author yuwei
 */
public class Config {
    /**
     * 逻辑表后占位符长度
     * eg.
     * user  ====  user_0000
     * @return   逻辑表后占位符长度
     */
    private int suffixLength = 4;
    /**
     * 分片数量
     */
    private int shardingSize;

    public int getSuffixLength() {
        return suffixLength;
    }

    public void setSuffixLength(int suffixLength) {
        this.suffixLength = suffixLength;
    }

    public int getShardingSize() {
        return shardingSize;
    }

    public void setShardingSize(int shardingSize) {
        this.shardingSize = shardingSize;
    }
}
