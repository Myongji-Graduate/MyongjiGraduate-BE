package com.plzgraduate.myongjigraduatebe.lecture.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

  List<Lecture> findAllByLectureCodeIsIn(Collection<LectureCode> lectureCode);

  Optional<Lecture> findById(long lectureId);

  Optional<Lecture> findByLectureCode(LectureCode duplicatedCode);

  @Query(value = "select l from Lecture l where l.lectureCode in (select l2.duplicatedLectureCode from Lecture l2 where l2.duplicatedLectureCode is not null)")
  List<Lecture> findAllDuplicatedLectures();

}
