package com.plzgraduate.myongjigraduatebe.lecture.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

  List<Lecture> findAllByLectureCodeIsIn(Collection<LectureCode> lectureCode);
}
