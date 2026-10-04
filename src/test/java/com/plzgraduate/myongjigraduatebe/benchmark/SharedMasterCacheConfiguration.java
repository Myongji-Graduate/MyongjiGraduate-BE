package com.plzgraduate.myongjigraduatebe.benchmark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.FindOptionalMandatoryPolicyPort;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.OptionalMandatoryPolicyPersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindBasicAcademicalCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCommonCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCoreCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindMajorPersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@TestConfiguration
class SharedMasterCacheConfiguration {
    @Bean
    SharedMasterCache sharedMasterCache(RedisConnectionFactory factory, ObjectMapper mapper) {
        var manager = RedisCacheManager.builder(factory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(24)))
            .build();
        manager.afterPropertiesSet();
        return new SharedMasterCache(manager.getCache("benchmarkMaster"), mapper);
    }

    @Bean
    @Primary
    FindCommonCulturePort cachedCommon(FindCommonCulturePersistenceAdapter delegate, SharedMasterCache cache) {
        return user -> {
            if (cache.mode == SharedMasterCache.Mode.PERSONAL_ONLY) return delegate.findCommonCulture(user);
            return cache.get(cache.key("common", user.getEntryYear(), user.getEnglishLevel()),
                    () -> delegate.findCommonCulture(user).stream().map(MasterCacheSnapshots.Common::from).toList())
                .stream().map(MasterCacheSnapshots.Common::toDomain).collect(Collectors.toSet());
        };
    }

    @Bean
    @Primary
    FindCoreCulturePort cachedCore(FindCoreCulturePersistenceAdapter delegate, SharedMasterCache cache) {
        return user -> {
            if (cache.mode == SharedMasterCache.Mode.PERSONAL_ONLY) return delegate.findCoreCulture(user);
            return cache.get(cache.key("core", user.getEntryYear()),
                    () -> delegate.findCoreCulture(user).stream().map(MasterCacheSnapshots.Core::from).toList())
                .stream().map(MasterCacheSnapshots.Core::toDomain).collect(Collectors.toSet());
        };
    }

    @Bean
    @Primary
    FindMajorPort cachedMajor(FindMajorPersistenceAdapter delegate, SharedMasterCache cache) {
        return major -> {
            if (cache.mode == SharedMasterCache.Mode.PERSONAL_ONLY) return delegate.findMajor(major);
            return cache.get(cache.key("major", major),
                    () -> delegate.findMajor(major).stream().map(MasterCacheSnapshots.Major::from).toList())
                .stream().map(MasterCacheSnapshots.Major::toDomain).collect(Collectors.toSet());
        };
    }

    @Bean
    @Primary
    FindBasicAcademicalCulturePort cachedBasic(
        FindBasicAcademicalCulturePersistenceAdapter delegate, SharedMasterCache cache) {
        return new FindBasicAcademicalCulturePort() {
            @Override
            public Set<BasicAcademicalCultureLecture> findBasicAcademicalCulture(String major, int entryYear) {
                if (cache.mode == SharedMasterCache.Mode.PERSONAL_ONLY) {
                    return delegate.findBasicAcademicalCulture(major, entryYear);
                }
                return cache.get(cache.key("basic", major, entryYear),
                        () -> delegate.findBasicAcademicalCulture(major, entryYear).stream()
                            .map(MasterCacheSnapshots.Basic::from).toList())
                    .stream().map(MasterCacheSnapshots.Basic::toDomain).collect(Collectors.toSet());
            }

            @Override
            public Set<BasicAcademicalCultureLecture> findDuplicatedLecturesBetweenMajors(User user) {
                // This query includes the individual student's taken lectures and is not shared.
                return delegate.findDuplicatedLecturesBetweenMajors(user);
            }
        };
    }

    @Bean
    @Primary
    FindOptionalMandatoryPolicyPort cachedPolicy(
        OptionalMandatoryPolicyPersistenceAdapter delegate, SharedMasterCache cache) {
        return new FindOptionalMandatoryPolicyPort() {
            @Override
            public List<OptionalMandatoryPolicy> findActivePolicies(String major, int year, MajorType type) {
                if (cache.mode == SharedMasterCache.Mode.PERSONAL_ONLY) {
                    return delegate.findActivePolicies(major, year, type);
                }
                return cache.get(cache.key("policy-major", major, year, type),
                        () -> delegate.findActivePolicies(major, year, type).stream()
                            .map(MasterCacheSnapshots.Policy::from).toList())
                    .stream().map(MasterCacheSnapshots.Policy::toDomain).toList();
            }

            @Override
            public List<OptionalMandatoryPolicy> findActiveBasicPolicies(String major, int year, MajorType type) {
                if (cache.mode == SharedMasterCache.Mode.PERSONAL_ONLY) {
                    return delegate.findActiveBasicPolicies(major, year, type);
                }
                return cache.get(cache.key("policy-basic", major, year, type),
                        () -> delegate.findActiveBasicPolicies(major, year, type).stream()
                            .map(MasterCacheSnapshots.Policy::from).toList())
                    .stream().map(MasterCacheSnapshots.Policy::toDomain).toList();
            }
        };
    }
}
