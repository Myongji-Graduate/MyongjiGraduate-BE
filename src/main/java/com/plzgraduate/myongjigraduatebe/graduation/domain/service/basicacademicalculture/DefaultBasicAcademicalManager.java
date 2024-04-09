package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DefaultBasicAcademicalManager implements BasicAcademicalManager {

	@Override
	public boolean isSatisfied(User user) {
		return false;
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, Set<BasicAcademicalCultureLecture> graduationLectures,
		int basicAcademicalCredit) {

		Set<Lecture> basicAcademicalLectures = convertToLectureSet(graduationLectures);

		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> basicAcademicalLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectureInventory.handleFinishedTakenLectures(removedTakenLecture);

		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			PRIMARY_BASIC_ACADEMICAL_CULTURE.getName(), true, basicAcademicalCredit);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);

		return DetailGraduationResult.create(PRIMARY_BASIC_ACADEMICAL_CULTURE, basicAcademicalCredit,
			List.of(detailCategoryResult));
	}
}
