package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public interface SearchLectureUseCase {
	List<Lecture> searchLectures(String type, String keyword);
}
