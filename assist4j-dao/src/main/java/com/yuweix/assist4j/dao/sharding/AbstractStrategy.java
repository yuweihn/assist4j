package com.yuweix.assist4j.dao.sharding;


import java.util.Map;


/**
 * @author yuwei
 */
public abstract class AbstractStrategy implements Strategy {
    public static final String FIELD_CONF_MAP_NAME = "confMap";
    protected static Map<String, Config> confMap;
}
