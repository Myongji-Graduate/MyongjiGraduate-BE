package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;

public interface SearchLecturePort {

	List<Lecture> searchLectureByNameOrCode(String type, String keyword);
}
