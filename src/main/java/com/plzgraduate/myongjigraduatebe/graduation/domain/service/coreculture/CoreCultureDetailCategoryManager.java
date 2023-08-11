package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import static com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester.*;

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
	private static final Lecture 과학과기술_예외_과목 = Lecture.from("KMA02136");
	private static final Set<Lecture> 문화와예술_예외_과목 = Set.of(
		Lecture.from("KMA02155"),
		Lecture.from("KMA02156"));

	public DetailCategoryResult generate(StudentInformation studentInformation, Set<TakenLecture> takenLectures,
		Set<CoreCulture> graduationLectures, CoreCultureCategory category) {
		Set<Lecture> graduationCoreCultureLectures = categorizeCommonCultures(graduationLectures, category);
		Set<TakenLecture> finishedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectures.stream()
			.filter(takenLecture -> graduationCoreCultureLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				finishedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectures.removeAll(finishedTakenLecture);

		int freeElectiveLeftCredit = calculateFreeElectiveLeftCredit(studentInformation, taken);
		int normalLeftCredit = calculateNormalLeftCredit(taken, finishedTakenLecture, category.getTotalCredit());
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
		if (ICT_DEPARTMENTS.contains(studentInformation.getDepartment()) && (taken.contains(과학과기술_예외_과목))) {
			taken.remove(과학과기술_예외_과목);
			return 3;
		}
		return 0;
	}

	private int calculateNormalLeftCredit(Set<Lecture> taken, Set<TakenLecture> finishedTakenLecture,
		int categoryTotalCredit) {
		int normalLeftCredit = finishedTakenLecture.stream()
			.filter(takenLecture -> 문화와예술_예외_과목.contains(takenLecture.getLecture())
				&& takenLecture.getYear() == 2022
				&& takenLecture.getSemester() == FIRST)
			.mapToInt(takenLecture -> takenLecture.getLecture().getCredit())
			.sum();
		taken.removeAll(문화와예술_예외_과목);

		int totalTakenCredit = taken.stream()
			.mapToInt(Lecture::getCredit)
			.sum();
		if (totalTakenCredit > categoryTotalCredit) {
			normalLeftCredit += totalTakenCredit - categoryTotalCredit;
		}
		return normalLeftCredit;
	}
}
