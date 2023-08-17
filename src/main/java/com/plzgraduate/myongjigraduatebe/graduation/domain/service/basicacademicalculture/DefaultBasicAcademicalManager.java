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
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DefaultBasicAcademicalManager implements BasicAcademicalManager {

	@Override
	public boolean isSatisfied(StudentInformation studentInformation) {
		return false;
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(StudentInformation studentInformation,
		TakenLectureInventory takenLectureInventory, Set<BasicAcademicalCulture> graduationLectures,
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
			BASIC_ACADEMICAL_CULTURE.getName(), true, basicAcademicalCredit);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);

		return DetailGraduationResult.create(BASIC_ACADEMICAL_CULTURE, basicAcademicalCredit,
			List.of(detailCategoryResult));
	}
}
