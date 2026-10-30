package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.TakenLecturePersistenceAdapter;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration(proxyBeanMethods = false)
public class TakenLectureCacheConfiguration {
    @Bean(destroyMethod = "close")
    TakenLectureCacheStore takenLectureCacheStore(LettuceConnectionFactory factory,
        @Value("${cache.taken-lecture.timeout:200ms}") Duration timeout,
        @Value("${cache.taken-lecture.mode:off}") String mode) {
        if (TakenLectureCache.Mode.valueOf(mode.toUpperCase(Locale.ROOT)) == TakenLectureCache.Mode.OFF) {
            return new TakenLectureCacheStore() {
                @Override
                public String get(String key) { return null; }
                @Override
                public void put(String key, String value, Duration ttl) {
                    // OFF mode deliberately avoids opening a Redis connection or storing values.
                }
            };
        }
        return new TakenLectureRedisStore(factory, timeout);
    }

    @Bean
    TakenLectureCache takenLectureCache(TakenLectureCacheStore store, ObjectMapper mapper, MeterRegistry registry,
        @Value("${cache.taken-lecture.mode:off}") String mode,
        @Value("${cache.taken-lecture.namespace:local}") String namespace,
        @Value("${cache.taken-lecture.ttl:10m}") Duration ttl,
        @Value("${cache.taken-lecture.cooldown:5s}") Duration cooldown,
        @Value("${cache.taken-lecture.max-concurrent:16}") int maxConcurrent,
        @Value("${cache.taken-lecture.local-entries:128}") int localEntries) {
        return new TakenLectureCache(store,
            mapper,
            registry,
            new TakenLectureCache.Options(TakenLectureCache.Mode.valueOf(mode.toUpperCase(Locale.ROOT)), namespace, ttl, cooldown, maxConcurrent, localEntries),
            System::nanoTime);
    }

    @Bean
    @Primary
    FindTakenLecturePort cachedTakenLectures(TakenLecturePersistenceAdapter delegate,
        TakenLectureCache cache) {
        return new CachedTakenLectureAdapter(delegate, delegate, cache);
    }
}
