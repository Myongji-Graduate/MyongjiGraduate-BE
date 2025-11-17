package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.UserTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport; // ★ 여기!
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserTimetableRepositoryTest extends PersistenceTestSupport { //

    @Autowired private UserTimetableRepository userTimetableRepository;
    @Autowired private EntityManager em;

    private UserJpaEntity persistUser(String email) {
        // NOT NULL 제약 있는 필드들 꼭 채워주세요(프로젝트 엔티티 제약에 맞춰 보강)
        UserJpaEntity u = UserJpaEntity.builder()
                .authId("auth-"+email)
                .password("pw")
                .studentNumber("60191656")
                .build();
        em.persist(u);
        return u;
    }

    private TimetableJpaEntity persistTimetable(Long id, String code, int year, int semester) {
        // TimetableJpaEntity has @GeneratedValue id and several non-null fields.
        // We DO NOT set id explicitly; we must populate required columns.
        String div = String.format("%04d", id % 10000);

        TimetableJpaEntity t = TimetableJpaEntity.builder()
                .classDivision(div)
                .lectureCode(code)
                .name("기초프로그래밍")
                .credit(3)
                .campus("인문")
                .year(year)
                .semester(semester)
                .maxStudent(50)
                .koreanCode("인소102")
                .department("인공지능·소프트웨어융합대학")
                .build();
        em.persist(t);
        return t;
    }

    private UserTimetableJpaEntity persistUserTimetable(UserJpaEntity u, TimetableJpaEntity t, int year, int semester) {
        UserTimetableJpaEntity ut = UserTimetableJpaEntity.builder()
                .user(u)
                .timetable(t)
                .year(year)
                .semester(semester)
                .build();
        em.persist(ut);
        return ut;
    }

    @Test
    @DisplayName("findByUser_IdAndYearAndSemester: 특정 유저/학기 시간표 전부")
    void findByUserAndSemester() {
        UserJpaEntity user = persistUser("u1@test.com");
        TimetableJpaEntity t1 = persistTimetable(101L, "A001", 2025, 2);
        TimetableJpaEntity t2 = persistTimetable(102L, "B002", 2025, 2);
        TimetableJpaEntity t3 = persistTimetable(103L, "C003", 2024, 2);

        persistUserTimetable(user, t1, 2025, 2);
        persistUserTimetable(user, t2, 2025, 2);
        persistUserTimetable(user, t3, 2024, 2); // 다른 학기

        em.flush(); em.clear();

        List<UserTimetableJpaEntity> found =
                userTimetableRepository.findByUser_IdAndYearAndSemester(user.getId(), 2025, 2);

        assertThat(found).hasSize(2);
        assertThat(found).extracting(ut -> ut.getTimetable().getId())
                .containsExactlyInAnyOrder(t1.getId(), t2.getId());
    }

    @Test
    @DisplayName("findByUser_IdAndTimetable_Id: 이미 담았는지 여부")
    void findByUserAndTimetable() {
        UserJpaEntity user = persistUser("u2@test.com");
        TimetableJpaEntity t1 = persistTimetable(201L, "Z100", 2025, 1);
        TimetableJpaEntity t2 = persistTimetable(202L, "Z200", 2025, 1);

        persistUserTimetable(user, t1, 2025, 1);

        em.flush(); em.clear();

        Optional<UserTimetableJpaEntity> hit =
                userTimetableRepository.findByUser_IdAndTimetable_Id(user.getId(), t1.getId());
        Optional<UserTimetableJpaEntity> miss =
                userTimetableRepository.findByUser_IdAndTimetable_Id(user.getId(), t2.getId());

        assertThat(hit).isPresent();
        assertThat(miss).isNotPresent();
    }

    @Test
    @DisplayName("deleteByUser_IdAndYearAndSemester: 특정 학기 전부 삭제")
    void deleteByUserAndSemester() {
        UserJpaEntity user = persistUser("u3@test.com");
        TimetableJpaEntity t1 = persistTimetable(301L, "D001", 2024, 2);
        TimetableJpaEntity t2 = persistTimetable(302L, "D002", 2024, 2);
        TimetableJpaEntity t3 = persistTimetable(303L, "D003", 2025, 2);

        persistUserTimetable(user, t1, 2024, 2);
        persistUserTimetable(user, t2, 2024, 2);
        persistUserTimetable(user, t3, 2025, 2);

        em.flush(); em.clear();

        userTimetableRepository.deleteByUser_IdAndYearAndSemester(user.getId(), 2024, 2);

        em.flush(); em.clear();

        List<UserTimetableJpaEntity> remain2024 =
                userTimetableRepository.findByUser_IdAndYearAndSemester(user.getId(), 2024, 2);
        List<UserTimetableJpaEntity> remain2025 =
                userTimetableRepository.findByUser_IdAndYearAndSemester(user.getId(), 2025, 2);

        assertThat(remain2024).isEmpty();
        assertThat(remain2025).hasSize(1);
        assertThat(remain2025.getFirst().getTimetable().getId()).isEqualTo(t3.getId());
    }

    @Test
    @DisplayName("@Query findTimetablesOfUser: Timetable만 바로 조회")
    void findTimetablesOfUser() {
        UserJpaEntity user = persistUser("u4@test.com");
        TimetableJpaEntity t1 = persistTimetable(401L, "E001", 2025, 1);
        TimetableJpaEntity t2 = persistTimetable(402L, "E002", 2025, 1);
        TimetableJpaEntity t3 = persistTimetable(403L, "E003", 2025, 2);

        persistUserTimetable(user, t1, 2025, 1);
        persistUserTimetable(user, t2, 2025, 1);
        persistUserTimetable(user, t3, 2025, 2);

        em.flush(); em.clear();

        List<TimetableJpaEntity> tt = userTimetableRepository.findTimetablesOfUser(user.getId(), 2025, 1);

        assertThat(tt).extracting(TimetableJpaEntity::getId)
                .containsExactlyInAnyOrder(t1.getId(), t2.getId());
    }
}