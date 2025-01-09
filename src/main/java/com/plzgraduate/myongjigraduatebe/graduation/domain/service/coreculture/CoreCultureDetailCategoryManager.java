package com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture;

import static com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester.FIRST;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CoreCultureDetailCategoryManager {

	private static final List<String> ICT_DEPARTMENTS = List.of(
		"응용소프트웨어",
		"데이터테크놀로지",
		"디지털콘텐츠디자인"
	);
	private static final Lecture 과학과기술_예외_과목 = Lecture.from("KMA02136");
	private static final Set<Lecture> 문화와예술_예외_과목 = Set.of(
		Lecture.from("KMA02155"),
		Lecture.from("KMA02156")
	);

	public DetailCategoryResult generate(
		User user, TakenLectureInventory takenLectureInventory,
		Set<CoreCulture> graduationLectures, CoreCultureCategory category
	) {
		if (user.getStudentCategory() == StudentCategory.TRANSFER) {
			return DetailCategoryResult.create(category.getName(), true, 0);
		}

		Set<Lecture> graduationCoreCultureLectures = categorizeCoreCultures(
			graduationLectures,
			category
		);
		Set<TakenLecture> finishedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();
		takenLectureInventory.getTakenLectures()
			.stream()
			.filter(
				takenLecture -> graduationCoreCultureLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				finishedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);

		DetailCategoryResult commonCultureDetailCategoryResult = DetailCategoryResult.create(
			category.getName(), true, category.getTotalCredit());
		calculateFreeElectiveLeftCredit(user, taken, commonCultureDetailCategoryResult);
		calculateNormalLeftCredit(taken, finishedTakenLecture, commonCultureDetailCategoryResult);
		commonCultureDetailCategoryResult.calculate(taken, graduationCoreCultureLectures);

		return commonCultureDetailCategoryResult;
	}

	private Set<Lecture> categorizeCoreCultures(
		Set<CoreCulture> graduationLectures,
		CoreCultureCategory category
	) {
		return graduationLectures.stream()
			.filter(coreCulture -> coreCulture.getCoreCultureCategory() == category)
			.map(CoreCulture::getLecture)
			.collect(Collectors.toSet());
	}

	private void calculateFreeElectiveLeftCredit(
		User user, Set<Lecture> taken,
		DetailCategoryResult commonCultureDetailCategoryResult
	) {
		if (ICT_DEPARTMENTS.contains(user.getPrimaryMajor()) && (taken.contains(과학과기술_예외_과목))) {
			taken.remove(과학과기술_예외_과목);
			int exceptionLectureCredit = 3;
			commonCultureDetailCategoryResult.addFreeElectiveLeftCredit(exceptionLectureCredit);
		}
	}

	private void calculateNormalLeftCredit(
		Set<Lecture> taken,
		Set<TakenLecture> finishedTakenLecture,
		DetailCategoryResult commonCultureDetailCategoryResult
	) {
		List<TakenLecture> cultureAndArtExceptionLectures = finishedTakenLecture.stream()
			.filter(takenLecture -> 문화와예술_예외_과목.contains(takenLecture.getLecture())
				&& takenLecture.getYear() == 2022
				&& takenLecture.getSemester() == FIRST)
			.collect(Collectors.toList());
		if (!cultureAndArtExceptionLectures.isEmpty()) {
			cultureAndArtExceptionLectures.stream()
				.map(TakenLecture::getLecture)
				.forEach(taken::remove);
			int normalLeftCredit = cultureAndArtExceptionLectures.stream()
				.mapToInt(exceptionLecture -> exceptionLecture.getLecture()
					.getCredit())
				.sum();
			commonCultureDetailCategoryResult.addNormalLeftCredit(normalLeftCredit);
		}
	}
}
