package com.plzgraduate.myongjigraduatebe.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.entity.User;

public interface TakenLectureRepository extends JpaRepository<TakenLecture, Long> {
  @Query("select distinct t from TakenLecture t join fetch t.lecture where t.user = :user")
  List<TakenLecture> findAllByUserWithFetchJoin(@Param("user") User user);

  @Transactional
  @Modifying
  @Query("delete from TakenLecture t where t.user = :user and t.lecture in :lectures")
  void deleteAllByUserAndLectureIsIn(
      @Param("user") User user,
      @Param("lectures") List<Lecture> deletedLectures
  );
}
