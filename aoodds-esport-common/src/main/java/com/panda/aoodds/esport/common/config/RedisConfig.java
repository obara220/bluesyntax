package com.panda.aoodds.esport.common.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.panda.aoodds.esport.common.service.RedisService;
import com.panda.aoodds.esport.common.service.RedisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Redis配置类
 * Created by macro on 2020/3/2.
 */
@Slf4j
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * redis数据库公共key,缓存1天
     */
    public static final String REDIS_KEY_DATABASE = "panda-merge";
    /**
     * redis数据库 linkid缓存位置
     */
    public static final String REDIS_KEY_LINKID = REDIS_KEY_DATABASE + ":linkid:";

    /**
     * 5分钟
     */
    public static final Integer REDIS_FIVE_MINS_TIME = 5 * 60;
    /**
     * 自定义缓存时间30分钟
     */
    public static final Integer REDIS_MY_TIME = 60 * 30;
    /**
     * 缓存1小时
     */
    public static final Integer REDIS_HOUR_TIME = 60 * 60;
    /**
     * 默认缓存一天
     */
    public static final Integer REDIS_DEFAULT_TIME = 60 * 60 * 24;
    /**
     * 缓存一周
     */
    public static final Integer REDIS_WEEK_TIME = 7 * 60 * 60 * 24;
    /**
     * 缓存一个月
     */
    public static final Integer REDIS_MONTH_TIME = 30 * 60 * 60 * 24;
    /**
     * 缓存一年
     */
    public static final Integer REDIS_YEAR_TIME = 30 * 60 * 60 * 24 * 12;

    /**
     * 设置 redis 数据默认过期时间，默认1天
     * 设置@cacheable 序列化方式
     *
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer)).entryTtl(Duration.ofDays(1));
        return configuration;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //使用FastJsonRedisSerializer进行序列化和反序列化（默认使用jdk序列化方式）
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        // 全局开启AutoType+
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        return template;
    }

    /**
     * 自定义缓存key生成策略
     * 使用方法 @Cacheable(keyGenerator="keyGenerator")
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(JSON.toJSONString(obj).hashCode());
            }
            return sb.toString();
        };
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("redis读异常：key=[{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("redis写异常：key=[{}]", key, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("redis删异常：key=[{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("redis清除异常：", e);
            }
        };
        return cacheErrorHandler;
    }

    @Bean
    public RedisService redisService() {
        return new RedisServiceImpl();
    }
}
