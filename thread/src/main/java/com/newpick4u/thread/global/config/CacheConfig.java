package com.newpick4u.thread.global.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    // 기본 설정
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
        .defaultCacheConfig()
        .disableCachingNullValues()
        .computePrefixWith(CacheKeyPrefix.simple())
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()))
        .entryTtl(Duration.ofMinutes(10)); // 전체 기본 TTL: 10분

    // 캐시 이름별 TTL 설정
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

    cacheConfigurations.put("threads", RedisCacheConfiguration
        .defaultCacheConfig()
        .disableCachingNullValues()
        .computePrefixWith(CacheKeyPrefix.simple())
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()))
        .entryTtl(Duration.ofMinutes(5))); // threads 캐시는 5분

    cacheConfigurations.put("otherCache", RedisCacheConfiguration
        .defaultCacheConfig()
        .disableCachingNullValues()
        .computePrefixWith(CacheKeyPrefix.simple())
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer()))
        .entryTtl(Duration.ofMinutes(3))); // otherCache 캐시는 3분

    return RedisCacheManager
        .builder(connectionFactory)
        .cacheDefaults(defaultConfig) // 전체 기본 설정
        .withInitialCacheConfigurations(cacheConfigurations) // 개별 캐시 설정
        .build();
  }
}
