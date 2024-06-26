package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

@Component
public class BusinessBasicAcademicalGraduationManager implements BasicAcademicalGraduationManager {

	private static final int TWENTY = 20;
	private static final String BUSINESS_ADMINISTRATION = "경영학과";
	private static final String MANAGEMENT_INFORMATION = "경영정보학과";
	private static final String INTERNATIONAL_TRADE = "국제통상학과";
	private static final Set<Lecture> businessBefore20 = Set.of(
		Lecture.of("KMD02114", "미시경제학원론", 3, 0, null),
		Lecture.of("KMD02107", "경상통계학", 3, 0, null)
	);
	private static final Set<Lecture> internationBefore20 = Set.of(
		Lecture.of("KMD02114", "미시경제학원론", 3, 0, null),
		Lecture.of("KMD02115", "거시경제학원론", 3, 0, null)
	);

	@Override
	public boolean isSatisfied(String major) {
		return findBelongingCollege(major) == BUSINESS;
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, Set<BasicAcademicalCultureLecture> graduationLectures,
		int basicAcademicalCredit) {
		Set<Lecture> basicAcademicalLectures = convertToLectureSet(graduationLectures);

		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();
		Set<Lecture> finalBasicAcademicalLectures = resetBasicAcademicalLectureSet(basicAcademicalLectures,
			user);

		takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> finalBasicAcademicalLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectureInventory.handleFinishedTakenLectures(removedTakenLecture);

		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			"학문기초교양", true, basicAcademicalCredit);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);

		return DetailGraduationResult.createNonCategorizedGraduationResult(basicAcademicalCredit,
			List.of(detailCategoryResult));
	}

	private Set<Lecture> resetBasicAcademicalLectureSet(Set<Lecture> basicAcademicalLectures,
		User user) {
		if (!user.checkBeforeEntryYear(TWENTY))
			return basicAcademicalLectures;

		if (user.checkMajor(BUSINESS_ADMINISTRATION) || user.checkMajor(MANAGEMENT_INFORMATION)) {
			return new HashSet<>(businessBefore20);
		}
		if (user.checkMajor(INTERNATIONAL_TRADE)) {
			return new HashSet<>(internationBefore20);
		}
		return basicAcademicalLectures;
	}
}
