package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.TakenLecturePersistenceAdapter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

class TakenLectureCacheConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
        .withInitializer(context -> context.getBeanFactory().setConversionService(ApplicationConversionService.getSharedInstance()))
        .withUserConfiguration(TakenLectureCacheConfiguration.class)
        .withBean(LettuceConnectionFactory.class, LettuceConnectionFactory::new)
        .withBean(ObjectMapper.class, () -> new ObjectMapper().findAndRegisterModules())
        .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
        .withBean(DataSource.class, () -> mock(DataSource.class))
        .withBean(FindLecturePort.class, () -> mock(FindLecturePort.class))
        .withBean(TakenLecturePersistenceAdapter.class, () -> mock(TakenLecturePersistenceAdapter.class));

    @ParameterizedTest
    @EnumSource(TakenLectureCache.Mode.class)
    void productionConfigurationSelectsDecoratorWithoutAddingAnotherSharedRedisFactory(TakenLectureCache.Mode mode) {
        runner.withPropertyValues("cache.taken-lecture.mode=" + mode.name()).run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(TakenLectureCache.class).mode()).isEqualTo(mode);
            assertThat(context.getBean(FindTakenLecturePort.class)).isInstanceOf(CachedTakenLectureAdapter.class);
            assertThat(context.getBeansOfType(RedisConnectionFactory.class)).hasSize(1);
            assertThat(context.getBean(TakenLectureCache.class).key(1L, 2, 3)).isEqualTo("takenLectures:v3:local:1:2:3");
        });
    }

    @Test
    void defaultModeKeepsInitialDeploymentOnDatabase() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.getBean(TakenLectureCache.class).mode()).isEqualTo(TakenLectureCache.Mode.OFF);
        });
    }
}
