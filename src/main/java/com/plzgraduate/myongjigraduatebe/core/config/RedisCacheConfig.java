package com.plzgraduate.myongjigraduatebe.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConditionalOnBean(RedisConnectionFactory.class)
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // JDK 직렬화 사용
        RedisSerializationContext.SerializationPair<Object> jdkSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer());

        // 기본 TTL: 30분
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(jdkSerializer);

        // 캐시별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("takenLectures", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(jdkSerializer));

        // 졸업요건 공유 캐시 (학기 1~2회만 변경되는 마스터 데이터)
        RedisCacheConfiguration graduationRequirementConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeValuesWith(jdkSerializer);
        cacheConfigurations.put("commonCultures", graduationRequirementConfig);
        cacheConfigurations.put("coreCultures", graduationRequirementConfig);
        cacheConfigurations.put("basicAcademicalCultures", graduationRequirementConfig);
        cacheConfigurations.put("majorLectures", graduationRequirementConfig);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public SimpleCacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }
}