package com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel.*;

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
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class CommonCultureDetailCategoryManager {

	private static final List<String> CHRISTAIN_MANDATORY_LECTURE_CODE_LIST =
		List.of("KMA02100", "KMA00100", "KMA00101"); // 성경개론, 성서의이해, 성서와인간이해
	private static final List<String> ENGLISH_12_MANDATORY_LECTURE_CODE_LIST =
		List.of("KMA02106", "KMA02107", "KMA02108", "KMA02109"); // 영어1, 영어2, 영어회화1, 영어회화2
	private static final List<String> ENGLISH_34_MANDATORY_LECTURE_CODE_LIST =
		List.of("KMA02123", "KMA02124", "KMA02125", "KMA02126"); // 영어3, 영어4, 영어회화3, 영어회화4

	public DetailCategoryResult generate(User user, TakenLectureInventory takenLectureInventory,
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
			category.getName(), checkMandatorySatisfaction(user, takenLectureInventory, category),
			checkCategoryTotalCredit(user, category));
		commonCultureDetailCategoryResult.calculate(taken, graduationCommonCultureLectures);

		takenLectureInventory.handleFinishedTakenLectures(removedTakenLecture);

		return commonCultureDetailCategoryResult;
	}

	private int checkCategoryTotalCredit(User user, CommonCultureCategory commonCultureCategory) {
		if (user.getEnglishLevel() == FREE && commonCultureCategory == ENGLISH){
			return 0;
		}
		return commonCultureCategory.getTotalCredit();
	}

	private Set<Lecture> categorizeCommonCultures(Set<CommonCulture> graduationLectures,
		CommonCultureCategory category) {
		return graduationLectures.stream()
			.filter(commonCulture -> commonCulture.getCommonCultureCategory() == category)
			.map(CommonCulture::getLecture)
			.collect(Collectors.toSet());
	}

	private boolean checkMandatorySatisfaction(User user, TakenLectureInventory takenLectureInventory,
		CommonCultureCategory category) {
		if (category == CHRISTIAN_A) {
			return takenLectureInventory.getTakenLectures().stream()
				.anyMatch(
					takenLecture -> CHRISTAIN_MANDATORY_LECTURE_CODE_LIST
						.contains(takenLecture.getLecture().getLectureCode()));
		}
		if (user.getEnglishLevel() == ENG12 && category == ENGLISH) {
			return checkEnglish12Satisfaction(takenLectureInventory);
		}
		if (user.getEnglishLevel() == ENG34 && category == ENGLISH) {
			return checkEnglish34Satisfaction(takenLectureInventory);
		}
		return true;
	}

	private boolean checkEnglish12Satisfaction(TakenLectureInventory takenLectureInventory) {
		for (String lectureCode : ENGLISH_12_MANDATORY_LECTURE_CODE_LIST) {
			if (!takenLectureInventory.getCultureLectures().stream()
				.map(takenLecture -> takenLecture.getLecture().getLectureCode())
				.collect(Collectors.toList())
				.contains(lectureCode)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkEnglish34Satisfaction(TakenLectureInventory takenLectureInventory) {
		for (String lectureCode : ENGLISH_34_MANDATORY_LECTURE_CODE_LIST) {
			if (!takenLectureInventory.getCultureLectures().stream()
				.map(takenLecture -> takenLecture.getLecture().getLectureCode())
				.collect(Collectors.toList())
				.contains(lectureCode)) {
				return false;
			}
		}
		return true;
	}
}
