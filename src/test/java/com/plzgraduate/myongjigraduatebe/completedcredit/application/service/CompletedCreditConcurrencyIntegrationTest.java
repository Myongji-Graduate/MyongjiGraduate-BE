package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.FindCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.FindCompletedCreditAdapter;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.GenerateOrModifyCompletedCreditsAdapter;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper.CompletedCreditPersistenceMapper;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.UserPersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest(properties = {
    "spring.datasource.url=jdbc:tc:mysql:8.0.29:///completed-credit-concurrency",
    "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver",
    "spring.datasource.username=test",
    "spring.datasource.password=test",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "spring.datasource.hikari.transaction-isolation=TRANSACTION_REPEATABLE_READ"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = CompletedCreditConcurrencyIntegrationTest.Config.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class CompletedCreditConcurrencyIntegrationTest {

    @Configuration
    @EntityScan(basePackageClasses = {UserJpaEntity.class, CompletedCreditJpaEntity.class})
    @EnableJpaRepositories(basePackageClasses = {UserRepository.class, CompletedCreditRepository.class})
    @Import({FindCompletedCreditService.class, FindCompletedCreditAdapter.class,
        GenerateOrModifyCompletedCreditsAdapter.class, CompletedCreditPersistenceMapper.class,
        UserPersistenceAdapter.class, UserMapper.class})
    static class Config {
    }

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompletedCreditRepository completedCreditRepository;
    @Autowired
    private FindCompletedCreditUseCase findCompletedCreditUseCase;
    @Autowired
    private FindCompletedCreditPort findCompletedCreditPort;
    @Autowired
    private GenerateOrModifyCompletedCreditsAdapter adapter;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @MockBean
    private GenerateOrModifyCompletedCreditUseCase generateUseCase;

    @BeforeEach
    void useRealPersistenceWithFixedCalculation() {
        doAnswer(invocation -> {
            persistCalculation(invocation.getArgument(0));
            return null;
        }).when(generateUseCase).generateOrModifyCompletedCredit(org.mockito.ArgumentMatchers.any());
    }

    @DisplayName("동일 사용자의 동시 학점 조회는 기존 데이터 유무와 관계없이 모두 성공한다")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void concurrentRequests(boolean hasExistingCredits) throws Exception {
        Long userId = createUser(hasExistingCredits);
        CountDownLatch firstRead = new CountDownLatch(1);
        CountDownLatch releaseFirst = new CountDownLatch(1);
        CountDownLatch secondStarted = new CountDownLatch(1);
        CountDownLatch secondCalculation = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            // Read before replacement, as the real calculation service does.
            findCompletedCreditPort.findCompletedCredit(user);
            if (calls.incrementAndGet() == 1) {
                firstRead.countDown();
                assertThat(releaseFirst.await(10, TimeUnit.SECONDS)).isTrue();
            } else {
                secondCalculation.countDown();
            }
            persistCalculation(user);
            return null;
        }).when(generateUseCase).generateOrModifyCompletedCredit(org.mockito.ArgumentMatchers.any());

        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> findCompletedCreditUseCase.findCompletedCredits(userId));
            try {
                assertThat(firstRead.await(10, TimeUnit.SECONDS)).isTrue();
                var second = executor.submit(() -> {
                    secondStarted.countDown();
                    return findCompletedCreditUseCase.findCompletedCredits(userId);
                });
                assertThat(secondStarted.await(10, TimeUnit.SECONDS)).isTrue();
                assertThat(secondCalculation.await(300, TimeUnit.MILLISECONDS))
                    .as("The second request must wait before calculation, not just before writing")
                    .isFalse();
                releaseFirst.countDown();
                assertCredits(first.get(30, TimeUnit.SECONDS), 5);
                assertCredits(second.get(30, TimeUnit.SECONDS), 5);
            } finally {
                releaseFirst.countDown();
            }
        }
        assertStoredCredits(userId, 5);
        assertCredits(findCompletedCreditUseCase.findCompletedCredits(userId), 5);
    }

    @DisplayName("다른 사용자의 학점 조회는 먼저 시작한 사용자의 계산을 기다리지 않는다")
    @Test
    void differentUsersDoNotBlockEachOther() throws Exception {
        Long firstUser = createUser(true);
        Long otherUser = createUser(true);
        CountDownLatch firstRead = new CountDownLatch(1);
        CountDownLatch releaseFirst = new CountDownLatch(1);
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId().equals(firstUser)) {
                firstRead.countDown();
                assertThat(releaseFirst.await(10, TimeUnit.SECONDS)).isTrue();
            }
            persistCalculation(user);
            return null;
        }).when(generateUseCase).generateOrModifyCompletedCredit(org.mockito.ArgumentMatchers.any());

        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> findCompletedCreditUseCase.findCompletedCredits(firstUser));
            try {
                assertThat(firstRead.await(10, TimeUnit.SECONDS)).isTrue();
                var other = executor.submit(() -> findCompletedCreditUseCase.findCompletedCredits(otherUser));
                assertCredits(other.get(5, TimeUnit.SECONDS), 5);
            } finally {
                releaseFirst.countDown();
            }
            assertCredits(first.get(30, TimeUnit.SECONDS), 5);
        }
    }

    @DisplayName("실패한 요청이 롤백되면 기존 학점을 유지하고 다음 요청이 성공한다")
    @Test
    void rollbackKeepsExistingCredits() {
        Long userId = createUser(true);
        transaction().executeWithoutResult(status -> {
            findCompletedCreditUseCase.findCompletedCredits(userId);
            status.setRollbackOnly();
        });
        assertStoredCredits(userId, 3);
        assertCredits(findCompletedCreditUseCase.findCompletedCredits(userId), 5);
    }

    private void persistCalculation(User user) {
        adapter.generateOrModifyCompletedCredits(user, List.of(CompletedCredit.builder()
            .graduationCategory(GraduationCategory.COMMON_CULTURE)
            .totalCredit(10).takenCredit(5).build()));
    }

    private Long createUser(boolean withCredits) {
        return transaction().execute(status -> {
            String identifier = UUID.randomUUID().toString();
            UserJpaEntity entity = userRepository.saveAndFlush(UserJpaEntity.builder()
                .authId(identifier).password("test-only").studentNumber(identifier)
                .transferCredit(TransferCredit.empty().toString())
                .createdAt(Instant.now()).updatedAt(Instant.now()).build());
            if (withCredits) {
                completedCreditRepository.saveAndFlush(CompletedCreditJpaEntity.builder()
                    .userJpaEntity(entity).graduationCategory(GraduationCategory.COMMON_CULTURE)
                    .totalCredit(10).takenCredit(3).build());
            }
            return entity.getId();
        });
    }

    private void assertStoredCredits(Long userId, double takenCredit) {
        transaction().executeWithoutResult(status -> {
            var credits = completedCreditRepository.findAllByUserJpaEntity(
                UserJpaEntity.builder().id(userId).build());
            assertThat(credits).hasSize(1);
            assertThat(credits.getFirst().getTakenCredit()).isEqualTo(takenCredit);
        });
    }

    private void assertCredits(List<CompletedCredit> credits, double takenCredit) {
        assertThat(credits).hasSize(1);
        assertThat(credits.getFirst().getGraduationCategory()).isEqualTo(GraduationCategory.COMMON_CULTURE);
        assertThat(credits.getFirst().getTotalCredit()).isEqualTo(10);
        assertThat(credits.getFirst().getTakenCredit()).isEqualTo(takenCredit);
    }

    private TransactionTemplate transaction() {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        transaction.setIsolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ);
        transaction.setTimeout(20);
        return transaction;
    }
}
