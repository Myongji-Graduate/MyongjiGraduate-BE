package com.plzgraduate.myongjigraduatebe.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;

public interface TakenLectureRepository extends JpaRepository<TakenLecture, Long> {
}
