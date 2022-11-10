package com.plzgraduate.myongjigraduatebe.user.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.entity.User;

public interface TakenLectureRepository extends JpaRepository<TakenLecture, Long> {
  @Query("select distinct t from TakenLecture t join fetch t.lecture where t.user = :user")
  Set<TakenLecture> findAllByUserWithFetchJoin(@Param("user") User user);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("delete from TakenLecture t where t.user = :user and t.lecture in :lectures")
  void deleteAllByUserAndLectureIsIn(
      @Param("user") User user,
      @Param("lectures") List<Lecture> deletedLectures
  );

  @Query(value = "select t from TakenLecture t join fetch t.lecture where t.user.id = :id")
  List<TakenLecture> findAllByUserIdWithFetchJoin(@Param("id") long id);

  void deleteAllByIdIsIn(List<Long> id);
}
