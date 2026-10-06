package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.cache;

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
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "cache.graduation-master", name = "enabled", havingValue = "true")
public class GraduationMasterCacheConfiguration {
    @Bean
    GraduationMasterCache graduationMasterCache(
        RedisConnectionFactory factory, ObjectMapper mapper, MeterRegistry registry,
        @Value("${cache.graduation-master.namespace:}") String namespace,
        @Value("${cache.graduation-master.version:}") String version) {
        if (!namespace.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException(
                "Enabled graduation master cache requires a valid namespace");
        }
        String cacheVersion = resolveVersion(version);
        var config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(24))
            .disableCachingNullValues()
            .computePrefixWith(name -> "graduationMaster:v1:" + namespace + ":" + cacheVersion + "::");
        var manager = RedisCacheManager.builder(factory).cacheDefaults(config).build();
        manager.afterPropertiesSet();
        log.info("Graduation master cache enabled: namespace={}, version={}", namespace, cacheVersion);
        return new GraduationMasterCache(manager.getCache("graduationMaster"), mapper, registry);
    }

    static String resolveVersion(String configuredVersion) {
        // A new process gets a fresh namespace even when the same commit is redeployed.
        if (configuredVersion.isEmpty()) {
            return "startup-" + UUID.randomUUID();
        }
        if (!configuredVersion.matches("[A-Za-z0-9._-]+")) {
            throw new IllegalArgumentException("Invalid graduation master cache version");
        }
        return configuredVersion;
    }

    @Bean
    @Primary
    FindCommonCulturePort cachedCommon(FindCommonCulturePersistenceAdapter delegate, GraduationMasterCache cache) {
        return user -> cache.get(cache.key("common", user.getEntryYear(), user.getEnglishLevel()),
                MasterCacheSnapshots.Common.class,
                () -> delegate.findCommonCulture(user).stream().map(MasterCacheSnapshots.Common::from).toList())
            .stream().map(MasterCacheSnapshots.Common::toDomain).collect(Collectors.toSet());
    }

    @Bean
    @Primary
    FindCoreCulturePort cachedCore(FindCoreCulturePersistenceAdapter delegate, GraduationMasterCache cache) {
        return user -> cache.get(cache.key("core", user.getEntryYear()), MasterCacheSnapshots.Core.class,
                () -> delegate.findCoreCulture(user).stream().map(MasterCacheSnapshots.Core::from).toList())
            .stream().map(MasterCacheSnapshots.Core::toDomain).collect(Collectors.toSet());
    }

    @Bean
    @Primary
    FindMajorPort cachedMajor(FindMajorPersistenceAdapter delegate, GraduationMasterCache cache) {
        return major -> cache.get(cache.key("major", major), MasterCacheSnapshots.Major.class,
                () -> delegate.findMajor(major).stream().map(MasterCacheSnapshots.Major::from).toList())
            .stream().map(MasterCacheSnapshots.Major::toDomain).collect(Collectors.toSet());
    }

    @Bean
    @Primary
    FindBasicAcademicalCulturePort cachedBasic(
        FindBasicAcademicalCulturePersistenceAdapter delegate, GraduationMasterCache cache) {
        return new FindBasicAcademicalCulturePort() {
            @Override
            public Set<BasicAcademicalCultureLecture> findBasicAcademicalCulture(String major, int entryYear) {
                return cache.get(cache.key("basic", major, entryYear), MasterCacheSnapshots.Basic.class,
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
        OptionalMandatoryPolicyPersistenceAdapter delegate, GraduationMasterCache cache) {
        return new FindOptionalMandatoryPolicyPort() {
            @Override
            public List<OptionalMandatoryPolicy> findActivePolicies(String major, int year, MajorType type) {
                return cache.get(cache.key("policy-major", major, year, type), MasterCacheSnapshots.Policy.class,
                        () -> delegate.findActivePolicies(major, year, type).stream()
                            .map(MasterCacheSnapshots.Policy::from).toList())
                    .stream().map(MasterCacheSnapshots.Policy::toDomain).toList();
            }

            @Override
            public List<OptionalMandatoryPolicy> findActiveBasicPolicies(String major, int year, MajorType type) {
                return cache.get(cache.key("policy-basic", major, year, type), MasterCacheSnapshots.Policy.class,
                        () -> delegate.findActiveBasicPolicies(major, year, type).stream()
                            .map(MasterCacheSnapshots.Policy::from).toList())
                    .stream().map(MasterCacheSnapshots.Policy::toDomain).toList();
            }
        };
    }
}
