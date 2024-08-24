package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public interface FindLecturesUseCase {
	List<Lecture> findLecturesByIds(List<Long> lectureIds);

	List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes);
}
