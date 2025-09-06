package com.example.store.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Redis config.
 */
@Configuration
@EnableCaching
@EnableTransactionManagement
public class RedisConfig {

    /**
     * Cache manager.
     *
     * @param redisConnectionFactory the redis connection factory
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        RedisCacheConfiguration customerConfig = defaultConfig.entryTtl(Duration.ofMinutes(5));

        RedisCacheConfiguration productConfig = defaultConfig.entryTtl(Duration.ofMinutes(15));

        RedisCacheConfiguration orderConfig = defaultConfig.entryTtl(Duration.ofMinutes(8));

        RedisCacheConfiguration pagedConfig = defaultConfig.entryTtl(Duration.ofMinutes(3));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("customers", customerConfig);
        cacheConfigurations.put("products", productConfig);
        cacheConfigurations.put("orders", orderConfig);
        cacheConfigurations.put("pagedCustomers", pagedConfig);
        cacheConfigurations.put("pagedProducts", pagedConfig);
        cacheConfigurations.put("pagedOrders", pagedConfig);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
