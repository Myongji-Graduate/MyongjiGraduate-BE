package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.OptionalMandatoryPolicyPersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindBasicAcademicalCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCommonCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCoreCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindMajorPersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
class GraduationMasterCacheRedisIntegrationTest {
    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
        .withExposedPorts(6379);
    private static LettuceConnectionFactory factory;
    private final GraduationMasterCacheConfiguration config = new GraduationMasterCacheConfiguration();
    private final Lecture lecture = Lecture.of("TEST", "Test", 3, 0, "OLD");
    private String namespace;
    private GraduationMasterCache cache;

    @BeforeAll
    static void connect() {
        factory = new LettuceConnectionFactory(
            new RedisStandaloneConfiguration(REDIS.getHost(), REDIS.getMappedPort(6379)),
            LettuceClientConfiguration.builder().commandTimeout(Duration.ofSeconds(1)).build());
        factory.afterPropertiesSet();
        factory.start();
    }

    @AfterAll
    static void disconnect() {
        if (factory != null) {
            factory.destroy();
        }
    }

    @BeforeEach
    void createCache() {
        namespace = "test-" + UUID.randomUUID();
        cache = version("data-1");
    }

    private GraduationMasterCache version(String version) {
        return config.graduationMasterCache(factory, new ObjectMapper(), new SimpleMeterRegistry(), namespace, version);
    }

    @Test
    void automaticStartupVersionsDoNotReuseOldCacheOrDeletePersonalKeys() {
        var redis = new StringRedisTemplate(factory);
        String personalKey = "takenLectures::" + UUID.randomUUID();
        redis.opsForValue().set(personalKey, "personal", Duration.ofMinutes(10));
        String key = cache.key("major", "major");
        var firstStartup = version("");
        firstStartup.get(key, String.class, () -> List.of("old"));
        var nextStartup = version("");
        assertThat(nextStartup.get(key, String.class, () -> List.of("new"))).containsExactly("new");
        assertThat(firstStartup.get(key, String.class, () -> { throw new AssertionError(); })).containsExactly("old");
        assertThat(redis.opsForValue().get(personalKey)).isEqualTo("personal");
    }

    @Test
    void versionChangeIsolatesOldEntriesAndUses24HourTtl() {
        String key = cache.key("major", "major");
        cache.get(key, String.class, () -> List.of("old"));
        var next = version("data-2");
        assertThat(next.get(key, String.class, () -> List.of("new"))).containsExactly("new");
        assertThat(cache.get(key, String.class, () -> { throw new AssertionError(); })).containsExactly("old");
        var redis = new StringRedisTemplate(factory);
        assertThat(redis.getExpire("graduationMaster:v1:" + namespace + ":data-1::" + key, TimeUnit.SECONDS))
            .isBetween(86_390L, 86_400L);
        var otherEnvironment = config.graduationMasterCache(factory, new ObjectMapper(), new SimpleMeterRegistry(),
            namespace + "-other", "data-1");
        assertThat(otherEnvironment.get(key, String.class, () -> List.of("other"))).containsExactly("other");
    }

    @Test
    void majorSnapshotRemainsUnchangedAcrossRequests() {
        var delegate = mock(FindMajorPersistenceAdapter.class);
        when(delegate.findMajor("major")).thenReturn(Set.of(MajorLecture.of(lecture, "major", 1, 16, 17)));
        var port = config.cachedMajor(delegate, cache);
        port.findMajor("major").iterator().next().changeMandatoryToElectiveByEntryYearRange(20);
        var second = port.findMajor("major").iterator().next();
        assertThat(second.getIsMandatory()).isEqualTo(1);
        assertThat(second.getLecture()).usingRecursiveComparison().isEqualTo(lecture);
        verify(delegate, times(1)).findMajor("major");
    }

    @Test
    void commonAndCoreKeysShareAcrossUsersButSeparateApplicableConditions() {
        var commonDelegate = mock(FindCommonCulturePersistenceAdapter.class);
        var coreDelegate = mock(FindCoreCulturePersistenceAdapter.class);
        var common = CommonCulture.of(lecture, CommonCultureCategory.values()[0]);
        var core = CoreCulture.of(lecture, CoreCultureCategory.values()[0]);
        when(commonDelegate.findCommonCulture(any())).thenReturn(Set.of(common));
        when(coreDelegate.findCoreCulture(any())).thenReturn(Set.of(core));
        var commonPort = config.cachedCommon(commonDelegate, cache);
        var corePort = config.cachedCore(coreDelegate, cache);
        User first = student(20, EnglishLevel.ENG12);
        User same = student(20, EnglishLevel.ENG12);
        User otherEnglish = student(20, EnglishLevel.ENG34);
        User otherYear = student(21, EnglishLevel.ENG12);
        for (var user : List.of(first, same, otherEnglish, otherYear)) {
            assertThat(commonPort.findCommonCulture(user).iterator().next())
                .usingRecursiveComparison().isEqualTo(common);
            assertThat(corePort.findCoreCulture(user).iterator().next()).usingRecursiveComparison().isEqualTo(core);
        }
        verify(commonDelegate, times(3)).findCommonCulture(any());
        verify(coreDelegate, times(2)).findCoreCulture(any());
    }

    @Test
    void basicSnapshotsKeepPolicyFieldsButUserSpecificQueryIsNeverShared() {
        var delegate = mock(FindBasicAcademicalCulturePersistenceAdapter.class);
        var basic = BasicAcademicalCultureLecture.builder().lecture(lecture).college("college")
            .major("major").sourcePolicyKey("policy").startEntryYear(20).endEntryYear(25)
            .startTakenYear(2020).endTakenYear(2025).startTakenSemester(Semester.values()[0])
            .endTakenSemester(Semester.values()[0]).build();
        when(delegate.findBasicAcademicalCulture("major", 20)).thenReturn(Set.of(basic));
        var port = config.cachedBasic(delegate, cache);
        for (int i = 0; i < 2; i++) {
            assertThat(port.findBasicAcademicalCulture("major", 20).iterator().next())
                .usingRecursiveComparison().isEqualTo(basic);
        }
        verify(delegate, times(1)).findBasicAcademicalCulture("major", 20);
        User user = student(20, EnglishLevel.ENG12);
        port.findDuplicatedLecturesBetweenMajors(user);
        port.findDuplicatedLecturesBetweenMajors(user);
        verify(delegate, times(2)).findDuplicatedLecturesBetweenMajors(user);
    }

    @Test
    void policiesKeepCandidatesAndSeparateCategoryAndMajorType() {
        var delegate = mock(OptionalMandatoryPolicyPersistenceAdapter.class);
        var policy = OptionalMandatoryPolicy.builder().id(1L).name("policy").major("major")
            .requiredCount(1).requiredCredit(3)
            .candidateLectures(List.of(new OptionalMandatoryPolicy.CandidateLecture(lecture, "equivalent")))
            .build();
        when(delegate.findActivePolicies(any(), anyInt(), any())).thenReturn(List.of(policy));
        when(delegate.findActiveBasicPolicies(any(), anyInt(), any())).thenReturn(List.of(policy));
        var port = config.cachedPolicy(delegate, cache);
        for (int i = 0; i < 2; i++) {
            for (var type : MajorType.values()) {
                assertThat(port.findActivePolicies("major", 20, type).getFirst())
                    .usingRecursiveComparison().isEqualTo(policy);
                assertThat(port.findActiveBasicPolicies("major", 20, type).getFirst())
                    .usingRecursiveComparison().isEqualTo(policy);
            }
        }
        verify(delegate, times(MajorType.values().length)).findActivePolicies(any(), anyInt(), any());
        verify(delegate, times(MajorType.values().length)).findActiveBasicPolicies(any(), anyInt(), any());
    }

    private User student(int year, EnglishLevel english) {
        var user = mock(User.class);
        when(user.getEntryYear()).thenReturn(year);
        when(user.getEnglishLevel()).thenReturn(english);
        return user;
    }
}
