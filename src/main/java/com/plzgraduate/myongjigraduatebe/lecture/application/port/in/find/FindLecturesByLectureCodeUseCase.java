package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.find;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public interface FindLecturesByLectureCodeUseCase {
	List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes);
}
