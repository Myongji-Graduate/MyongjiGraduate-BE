package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.UserTimetableJpaEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserTimetableRepository extends JpaRepository<UserTimetableJpaEntity, Long> {
    // 특정 유저의 특정 학기 시간표 전체 조회
    List<UserTimetableJpaEntity> findByUser_IdAndYearAndSemester(Long userId, int year, int semester);

    // 특정 유저가 특정 timetable 강의를 이미 등록했는지 확인
    Optional<UserTimetableJpaEntity> findByUser_IdAndTimetable_Id(Long userId, Long timetableId);

    // 특정 유저의 특정 학기 시간표 전체 삭제 (replace 시 활용)
    void deleteByUser_IdAndYearAndSemester(Long userId, int year, int semester);

    @Query("select ut.timetable " +
            "from UserTimetableJpaEntity ut " +
            "where ut.user.id = :userId and ut.year = :year and ut.semester = :semester")
    List<TimetableJpaEntity> findTimetablesOfUser(@Param("userId") Long userId,
                                                  @Param("year") int year,
                                                  @Param("semester") int semester);
}
