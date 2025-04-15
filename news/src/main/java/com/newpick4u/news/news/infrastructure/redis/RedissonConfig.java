package com.newpick4u.news.news.infrastructure.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        return Redisson.create(); // application.yml에서 Redis 설정 자동 인식
    }
}
