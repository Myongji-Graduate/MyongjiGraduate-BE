package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

public class CoreCultureDetailCategoryManager {

	private static final List<String> ICT_DEPARTMENTS = List.of(
		"응용소프트웨어학과",
		"데이터테크놀로지학과",
		"디지털콘텐츠디자인학과");
	private static final Lecture EXCEPTION_LECTURE = Lecture.from("KMA02136");

	public DetailCategoryResult generate(StudentInformation studentInformation, Set<TakenLecture> takenLectures,
		Set<CoreCulture> graduationLectures, CoreCultureCategory category) {
		Set<Lecture> graduationCoreCultureLectures = categorizeCommonCultures(graduationLectures, category);
		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectures.stream()
			.filter(takenLecture -> graduationCoreCultureLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectures.removeAll(removedTakenLecture);

		int freeElectiveLeftCredit = calculateFreeElectiveLeftCredit(studentInformation, taken);
		int normalLeftCredit = calculateNormalLeftCredit(taken, category.getTotalCredit());
		DetailCategoryResult commonCultureDetailCategoryResult = DetailCategoryResult.create(
			category.getName(), true, category.getTotalCredit(), normalLeftCredit, freeElectiveLeftCredit);
		commonCultureDetailCategoryResult.calculate(taken, graduationCoreCultureLectures);

		return commonCultureDetailCategoryResult;
	}

	private Set<Lecture> categorizeCommonCultures(Set<CoreCulture> graduationLectures,
		CoreCultureCategory category) {
		return graduationLectures.stream()
			.filter(coreCulture -> coreCulture.getCoreCultureCategory() == category)
			.map(CoreCulture::getLecture)
			.collect(Collectors.toSet());
	}

	private int calculateFreeElectiveLeftCredit(StudentInformation studentInformation, Set<Lecture> taken) {
		if (ICT_DEPARTMENTS.contains(studentInformation.getDepartment()) && (taken.contains(EXCEPTION_LECTURE))) {
			taken.remove(EXCEPTION_LECTURE);
			return 3;
		}
		return 0;
	}

	private int calculateNormalLeftCredit(Set<Lecture> taken, int categoryTotalCredit) {
		int totalTakenCredit = taken.stream()
			.mapToInt(Lecture::getCredit)
			.sum();
		int normalLeftCredit = totalTakenCredit - categoryTotalCredit;
		return Math.max(normalLeftCredit, 0);
	}
}
