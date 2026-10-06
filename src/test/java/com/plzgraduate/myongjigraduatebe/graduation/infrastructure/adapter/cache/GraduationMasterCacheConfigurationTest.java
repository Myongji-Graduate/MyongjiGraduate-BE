package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.FindOptionalMandatoryPolicyPort;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.OptionalMandatoryPolicyPersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindBasicAcademicalCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCommonCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCoreCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindMajorPersistenceAdapter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;

class GraduationMasterCacheConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
        .withUserConfiguration(GraduationMasterCacheConfiguration.class)
        .withBean(ObjectMapper.class, ObjectMapper::new)
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .withBean(RedisConnectionFactory.class, () -> mock(RedisConnectionFactory.class))
        .withBean(FindCommonCulturePersistenceAdapter.class, () -> mock(FindCommonCulturePersistenceAdapter.class))
        .withBean(FindCoreCulturePersistenceAdapter.class, () -> mock(FindCoreCulturePersistenceAdapter.class))
        .withBean(FindMajorPersistenceAdapter.class, () -> mock(FindMajorPersistenceAdapter.class))
        .withBean(FindBasicAcademicalCulturePersistenceAdapter.class,
            () -> mock(FindBasicAcademicalCulturePersistenceAdapter.class))
        .withBean(OptionalMandatoryPolicyPersistenceAdapter.class,
            () -> mock(OptionalMandatoryPolicyPersistenceAdapter.class));

    @Test
    void disabledByDefaultAndKeepsOriginalPorts() {
        runner.run(context -> {
            assertThat(context).hasNotFailed().doesNotHaveBean(GraduationMasterCache.class);
            assertThat(context.getBean(FindMajorPort.class))
                .isSameAs(context.getBean(FindMajorPersistenceAdapter.class));
            assertThat(context).hasSingleBean(FindCommonCulturePort.class)
                .hasSingleBean(FindCoreCulturePort.class)
                .hasSingleBean(FindBasicAcademicalCulturePort.class)
                .hasSingleBean(FindOptionalMandatoryPolicyPort.class);
        });
    }

    @Test
    void enabledDecoratorsArePrimaryWithoutReplacingPersistenceAdapters() {
        runner.withPropertyValues("cache.graduation-master.enabled=true",
            "cache.graduation-master.namespace=test", "cache.graduation-master.version=data-1")
            .run(context -> {
                assertThat(context).hasNotFailed().hasSingleBean(GraduationMasterCache.class);
                assertThat(context.getBean(FindCommonCulturePort.class))
                    .isNotSameAs(context.getBean(FindCommonCulturePersistenceAdapter.class));
                assertThat(context.getBean(FindCoreCulturePort.class))
                    .isNotSameAs(context.getBean(FindCoreCulturePersistenceAdapter.class));
                assertThat(context.getBean(FindMajorPort.class))
                    .isNotSameAs(context.getBean(FindMajorPersistenceAdapter.class));
                assertThat(context.getBean(FindBasicAcademicalCulturePort.class))
                    .isNotSameAs(context.getBean(FindBasicAcademicalCulturePersistenceAdapter.class));
                assertThat(context.getBean(FindOptionalMandatoryPolicyPort.class))
                    .isNotSameAs(context.getBean(OptionalMandatoryPolicyPersistenceAdapter.class));
            });
    }

    @Test
    void enabledCacheFailsStartupWithoutNamespaceOrWithInvalidVersion() {
        runner.withPropertyValues("cache.graduation-master.enabled=true")
            .run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues("cache.graduation-master.enabled=true",
            "cache.graduation-master.namespace=test", "cache.graduation-master.version=bad:version")
            .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void enabledCacheCanStartWithoutManuallyConfiguredVersion() {
        runner.withPropertyValues("cache.graduation-master.enabled=true", "cache.graduation-master.namespace=test")
            .run(context -> assertThat(context).hasNotFailed().hasSingleBean(GraduationMasterCache.class));
    }

    @Test
    void omittedVersionIsUniqueAndDeploymentSuppliedVersionIsPreserved() {
        String first = GraduationMasterCacheConfiguration.resolveVersion("");
        String second = GraduationMasterCacheConfiguration.resolveVersion("");
        assertThat(first).matches("startup-[0-9a-f-]{36}").isNotEqualTo(second);
        assertThat(GraduationMasterCacheConfiguration.resolveVersion("gha-12345-2"))
            .isEqualTo("gha-12345-2");
    }
}
