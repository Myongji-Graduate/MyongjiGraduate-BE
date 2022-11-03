package com.plzgraduate.myongjigraduatebe.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.entity.User;

public interface TakenLectureRepository extends JpaRepository<TakenLecture, Long> {
  @Query("select distinct t from TakenLecture t join fetch t.lecture where t.user = :user")
  List<TakenLecture> findAllByUserWithFetchJoin(@Param("user") User user);
}
