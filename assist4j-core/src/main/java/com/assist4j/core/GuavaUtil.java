package com.assist4j.core;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


/**
 * @author wei
 */
public abstract class GuavaUtil {
    private static final Logger log = LoggerFactory.getLogger(GuavaUtil.class);

    /**
     * 缓存时间(单位：秒)
     */
    private static final long GUAVA_CACHE_TIME = 300;

    /**
     * 缓存操作对象
     */
    private static LoadingCache<String, String> GLOBAL_CACHE = null;

    static {
        try {
            GLOBAL_CACHE = loadCache(new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    if (log.isDebugEnabled()) {
                        log.debug("Guava Cache缓存值不存在，初始化空值，键名：{}", key);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            log.error("初始化Guava Cache出错", e);
        }
    }

    private static <K, V> LoadingCache<K, V> loadCache(CacheLoader<K, V> cacheLoader) throws Exception {
        LoadingCache<K, V> cache = CacheBuilder.newBuilder().expireAfterAccess(GUAVA_CACHE_TIME, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<K, V>() {
                    @Override
                    public void onRemoval(RemovalNotification<K, V> rn) {
                        if (log.isDebugEnabled()) {
                            log.debug("Guava Cache缓存回收成功，键：{}, 值：{}", rn.getKey(), rn.getValue());
                        }
                    }
                }).recordStats().build(cacheLoader);
        return cache;
    }

    public static boolean put(String key, String value) {
        try {
            GLOBAL_CACHE.put(key, value);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
            return true;
        } catch (Exception e) {
            log.error("设置缓存值出错", e);
            return false;
        }
    }

    public static boolean putAll(Map<String, String> map) {
        try {
            GLOBAL_CACHE.putAll(map);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
            return true;
        } catch (Exception e) {
            log.error("批量设置缓存值出错", e);
            return false;
        }
    }

    public static String get(String key) {
        String val = null;
        try {
            val = GLOBAL_CACHE.get(key);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("获取缓存值出错", e);
        }
        return val;
    }

    public static boolean remove(String key) {
        try {
            GLOBAL_CACHE.invalidate(key);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
            return true;
        } catch (Exception e) {
            log.error("移除缓存出错", e);
            return false;
        }
    }

    /**
     * 批量移除缓存
     */
    public static boolean removeAll(Iterable<String> keys) {
        try {
            GLOBAL_CACHE.invalidateAll(keys);
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
            return true;
        } catch (Exception e) {
            log.error("批量移除缓存出错", e);
            return false;
        }
    }

    /**
     * 清空所有缓存
     */
    public static boolean removeAll() {
        try {
            GLOBAL_CACHE.invalidateAll();
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
            return true;
        } catch (Exception e) {
            log.error("清空所有缓存出错", e);
            return false;
        }
    }

    /**
     * 获取缓存项数量
     */
    public static long size() {
        long size = 0;
        try {
            size = GLOBAL_CACHE.size();
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("获取缓存项数量出错", e);
        }
        return size;
    }

    /**
     * 获取所有缓存项的键
     */
    public static List<String> keys() {
        List<String> list = new ArrayList<String>();
        try {
            ConcurrentMap<String, String> map = GLOBAL_CACHE.asMap();
            for (Map.Entry<String, String> item : map.entrySet()) {
                list.add(item.getKey());
            }
            if (log.isDebugEnabled()) {
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
            }
        } catch (Exception e) {
            log.error("获取所有缓存项的键出错", e);
        }
        return list;
    }

    /**
     * 缓存命中率
     */
    public static double getHitRate() {
        return GLOBAL_CACHE.stats().hitRate();
    }

    /**
     * 加载新值的平均时间，单位为纳秒
     */
    public static double getAverageLoadPenalty() {
        return GLOBAL_CACHE.stats().averageLoadPenalty();
    }

    /**
     * 缓存项被回收的总数，不包括显式清除
     */
    public static long getEvictionCount() {
        return GLOBAL_CACHE.stats().evictionCount();
    }
}
