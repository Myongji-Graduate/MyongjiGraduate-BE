package com.plzgraduate.myongjigraduatebe.lecture.application.port.out;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public interface FindLecturePort {

	List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes);

	List<Lecture> findLecturesByIds(List<Long> lectureIds);
}
