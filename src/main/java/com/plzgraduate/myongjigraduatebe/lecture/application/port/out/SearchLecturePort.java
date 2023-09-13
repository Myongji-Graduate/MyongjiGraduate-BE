package com.plzgraduate.myongjigraduatebe.lecture.application.port.out;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public interface SearchLecturePort {
	List<Lecture> searchLectureByNameOrCode(String type, String keyword);
}
