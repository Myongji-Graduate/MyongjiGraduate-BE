package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface BasicAcademicalManager extends GraduationManager<BasicAcademicalCulture> {
	boolean isSatisfied(User user);

	default Set<Lecture> convertToLectureSet(Set<BasicAcademicalCulture> basicAcademicalCultures) {
		return basicAcademicalCultures.stream()
			.map(BasicAcademicalCulture::getLecture)
			.collect(Collectors.toSet());
	}
}
