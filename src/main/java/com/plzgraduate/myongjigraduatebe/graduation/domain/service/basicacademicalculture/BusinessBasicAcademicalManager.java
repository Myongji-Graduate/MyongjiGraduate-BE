package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.BASIC_ACADEMICAL_CULTURE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

public class BusinessBasicAcademicalManager implements BasicAcademicalManager {

	private static final int TWENTY = 20;
	private static final String BUSINESS_ADMINISTRATION = "경영학과";
	private static final String MANAGEMENT_INFORMATION = "경영정보학과";
	private static final String INTERNATIONAL_TRADE = "국제통상확과";
	private static final Set<Lecture> businessBefore20 = Set.of(
		Lecture.of("KMD02114", "미시경제학원론", 3, 0, null),
		Lecture.of("KMD02107", "경상통계학", 3, 0, null)
	);
	private static final Set<Lecture> internationBefore20 = Set.of(
		Lecture.of("KMD02114", "미시경제학원론", 3, 0, null),
		Lecture.of("KMD02115", "거시경제학원론", 3, 0, null)
	);

	@Override
	public boolean isSatisfied(StudentInformation studentInformation) {
		return false;
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(StudentInformation studentInformation,
		Set<TakenLecture> takenLectures,
		Set<BasicAcademicalCulture> graduationLectures, int basicAcademicalCredit) {
		Set<Lecture> basicAcademicalLectures = convertToLectureSet(graduationLectures);

		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();
		Set<Lecture> finalBasicAcademicalLectures = resetBasicAcademicalLectureSet(basicAcademicalLectures,
			studentInformation);

		takenLectures.stream()
			.filter(takenLecture -> finalBasicAcademicalLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectures.removeAll(removedTakenLecture);

		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			BASIC_ACADEMICAL_CULTURE.getName(), true, basicAcademicalCredit);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);

		return DetailGraduationResult.create(BASIC_ACADEMICAL_CULTURE, basicAcademicalCredit,
			List.of(detailCategoryResult));
	}

	private Set<Lecture> resetBasicAcademicalLectureSet(Set<Lecture> basicAcademicalLectures,
		StudentInformation studentInformation) {
		if (!studentInformation.checkBeforeEntryYear(TWENTY))
			return basicAcademicalLectures;

		if (studentInformation.checkDepartment(BUSINESS_ADMINISTRATION) || studentInformation.checkDepartment(
			MANAGEMENT_INFORMATION)) {
			return new HashSet<>(businessBefore20);
		}
		if (studentInformation.checkDepartment(INTERNATIONAL_TRADE)) {
			return new HashSet<>(internationBefore20);
		}
		return basicAcademicalLectures;
	}
}
