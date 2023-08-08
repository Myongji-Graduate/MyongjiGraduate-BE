package com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;

class CommonCultureDetailCategoryManager {

	public DetailCategoryResult generate(Set<TakenLecture> takenLectures,
		Set<CommonCulture> graduationLectures, CommonCultureCategory category) {
		DetailCategoryResult commonCultureDetailCategoryResult = DetailCategoryResult.create(
			category.getName(), category.getTotalCredit());
		Set<Lecture> taken = new HashSet<>();

		Set<Lecture> graduationCommonCultureLectures = categorizeCommonCultures(
			graduationLectures, category);

		takenLectures.stream()
			.filter(takenLecture -> graduationCommonCultureLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> taken.add(takenLecture.getLecture()));

		commonCultureDetailCategoryResult.calculate(taken, graduationCommonCultureLectures);

		return commonCultureDetailCategoryResult;
	}

	private Set<Lecture> categorizeCommonCultures(Set<CommonCulture> graduationLectures, CommonCultureCategory category) {
		return graduationLectures.stream()
			.filter(commonCulture -> commonCulture.getCommonCultureCategory() == category)
			.map(CommonCulture::getLecture)
			.collect(Collectors.toSet());
	}
}
