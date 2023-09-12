package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.find;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public interface FindLecturesByIdUseCase {
	List<Lecture> findLecturesByIds(List<Long> lectureIds);
}