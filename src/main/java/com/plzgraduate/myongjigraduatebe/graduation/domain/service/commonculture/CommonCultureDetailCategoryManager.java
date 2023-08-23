package com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

class CommonCultureDetailCategoryManager {

	private static final List<String> MANDATORY_LECTURE_CODE_LIST = List.of("KMA02100", "KMA00100", "KMA00101");

	public DetailCategoryResult generate(TakenLectureInventory takenLectureInventory,
		Set<CommonCulture> graduationLectures, CommonCultureCategory category) {
		Set<Lecture> graduationCommonCultureLectures = categorizeCommonCultures(
			graduationLectures, category);

		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> graduationCommonCultureLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});

		DetailCategoryResult commonCultureDetailCategoryResult = DetailCategoryResult.create(
			category.getName(), checkMandatorySatisfaction(takenLectureInventory, category), category.getTotalCredit());
		commonCultureDetailCategoryResult.calculate(taken, graduationCommonCultureLectures);

		takenLectureInventory.handleFinishedTakenLectures(removedTakenLecture);

		return commonCultureDetailCategoryResult;
	}

	private boolean checkMandatorySatisfaction(TakenLectureInventory takenLectureInventory,
		CommonCultureCategory category) {
		if (category == CommonCultureCategory.CHRISTIAN_A) {
			return takenLectureInventory.getTakenLectures().stream()
				.anyMatch(
					takenLecture -> MANDATORY_LECTURE_CODE_LIST.contains(takenLecture.getLecture().getLectureCode()));
		}
		return true;
	}

	private Set<Lecture> categorizeCommonCultures(Set<CommonCulture> graduationLectures,
		CommonCultureCategory category) {
		return graduationLectures.stream()
			.filter(commonCulture -> commonCulture.getCommonCultureCategory() == category)
			.map(CommonCulture::getLecture)
			.collect(Collectors.toSet());
	}
}
