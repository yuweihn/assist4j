package com.yuweix.assist4j.dao.sharding;


import java.util.Map;


/**
 * @author yuwei
 **/
public interface ShardingBeanHolder {
    Map<String, Config> getShardingConf();

    class Config {
        /**
         * 逻辑表后占位符长度
         * eg.
         * user  ====  user_0000
         * @return   逻辑表后占位符长度
         */
        private int suffixLength;
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
}
