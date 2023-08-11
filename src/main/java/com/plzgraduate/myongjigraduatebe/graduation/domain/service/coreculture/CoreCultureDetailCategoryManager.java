package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;

public class CoreCultureDetailCategoryManager {

	public DetailCategoryResult generate(Set<TakenLecture> takenLectures,
		Set<CoreCulture> graduationLectures, CoreCultureCategory category) {
		DetailCategoryResult commonCultureDetailCategoryResult = DetailCategoryResult.create(
			category.getName(), true, category.getTotalCredit());

		Set<Lecture> graduationCoreCultureLectures = categorizeCommonCultures(
			graduationLectures, category);

		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectures.stream()
			.filter(takenLecture -> graduationCoreCultureLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});

		commonCultureDetailCategoryResult.calculate(taken, graduationCoreCultureLectures);
		takenLectures.removeAll(removedTakenLecture);

		return commonCultureDetailCategoryResult;
	}

	private Set<Lecture> categorizeCommonCultures(Set<CoreCulture> graduationLectures,
		CoreCultureCategory category) {
		return graduationLectures.stream()
			.filter(coreCulture -> coreCulture.getCoreCultureCategory() == category)
			.map(CoreCulture::getLecture)
			.collect(Collectors.toSet());
	}
}
