package com.yuweix.assist4j.dao.sharding;



/**
 * 分片策略
 * @author yuwei
 */
public interface Strategy {
    /**
     * @param val                分片字段的值
     * @param suffixLength       占位符长度。如：0000,0001等等，则该值为4
     * @return   返回分片。如：0000,0001等等
     */
    String getShardingIndex(Object val, int suffixLength);
}
