package org.scalingmq.common.cache;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 * @author renyansong
 */
@Builder
@Getter
@Setter
@Slf4j
public class LocalCache<K, V> {

    private static final ScheduledThreadPoolExecutor REFRESH_CACHE_POOL = new ScheduledThreadPoolExecutor(1,
            r -> new Thread(r, "local-cache-refresh-thread"));

    private final Map<K, V> DATA = new ConcurrentHashMap<>();

    private volatile boolean init;

    /**
     * 缓存总数
     */
    private Integer cacheCount;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 时间单位
     */
    private TimeUnit timeUnit;

    /**
     * 缓存刷新时间
     */
    private Long refreshInterval;

    /**
     * 缓存加载
     */
    private CacheLoader cacheLoader;

    /**
     * 获取缓存
     * @param key 缓存key
     * @return 缓存value
     */
    public V get(K key) throws Exception {
        if (!init) {
            synchronized (this) {
                if (!init) {
                    init();
                    init = true;
                }
            }
        }
        V result = DATA.get(key);
        if (result == null) {
            result =  (V) cacheLoader.load(key);
            // TODO: 2022/10/9 异步加载模式
            if (result == null) {
                throw new RuntimeException("cache load error");
            }
            DATA.put(key, result);
        }
        return result;
    }

    private void init() {
        if (expireTime != null && refreshInterval != null) {
            throw new RuntimeException("can not implement refresh and expire as the same time");
        }
        if (expireTime != null) {
            // TODO: 2022/10/11 缓存过期实现
        }
        if (refreshInterval != null) {
            REFRESH_CACHE_POOL.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    for (K key : DATA.keySet()) {
                        try {
                            DATA.put(key, (V) cacheLoader.load(key));
                        } catch (Exception e) {
                            log.error("缓存刷新失败, key:{}", key, e);
                        }
                    }
                }
            }, refreshInterval, refreshInterval, timeUnit);
        }
    }

    /**
     * 缓存加载的接口
     */
    public interface CacheLoader {

        /**
         * 加载缓存
         * @param key 缓存key
         * @return 缓存值
         */
        Object load(Object key) throws Exception;

    }

}
