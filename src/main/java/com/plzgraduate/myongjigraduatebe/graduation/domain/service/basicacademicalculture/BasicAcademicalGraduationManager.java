package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.Set;
import java.util.stream.Collectors;

public interface BasicAcademicalGraduationManager extends
	GraduationManager<BasicAcademicalCultureLecture> {

	boolean isSatisfied(String major);

	default Set<Lecture> convertToLectureSet(
		Set<BasicAcademicalCultureLecture> basicAcademicalCultureLectures) {
		return basicAcademicalCultureLectures.stream()
			.map(BasicAcademicalCultureLecture::getLecture)
			.collect(Collectors.toSet());
	}
}
