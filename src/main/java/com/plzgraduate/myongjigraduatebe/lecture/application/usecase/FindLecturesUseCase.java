package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;

public interface FindLecturesUseCase {

	List<Lecture> findLecturesByIds(List<String> lectureIds);

	List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes);
}
