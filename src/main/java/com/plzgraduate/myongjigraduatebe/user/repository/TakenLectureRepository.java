package com.plzgraduate.myongjigraduatebe.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;

public interface TakenLectureRepository extends JpaRepository<TakenLecture, Long> {
}
