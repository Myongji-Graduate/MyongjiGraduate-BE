package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;

public interface FindLecturePort {

	List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes);

	List<Lecture> findLecturesByIds(List<Long> lectureIds);

	Lecture findLectureById(Long lectureId);
}
