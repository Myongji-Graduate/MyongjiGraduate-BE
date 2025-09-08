package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimetableRepository extends JpaRepository<TimetableJpaEntity, Long> {
    List<TimetableJpaEntity> findAllByYearAndSemester(int year, int semester);
    List<TimetableJpaEntity> findByYearAndSemesterAndNameContaining(int year, int semester, String keyword);

    List<TimetableJpaEntity> findByYearAndSemesterAndLectureCodeIn(int year, int semester, List<String> codes);

    @Query("select t from TimetableJpaEntity t where t.year = :year and t.semester = :semester and t.lectureCode not in :codes")
    List<TimetableJpaEntity> findByYearAndSemesterAndLectureCodeNotIn(@Param("year") int year,
                                                                      @Param("semester") int semester,
                                                                      @Param("codes") List<String> codes);

}