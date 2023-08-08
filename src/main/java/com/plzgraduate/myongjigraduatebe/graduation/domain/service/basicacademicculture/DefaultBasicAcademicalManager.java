package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicculture;

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

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DefaultBasicAcademicalManager implements BasicAcademicalManager {

	@Override
	public boolean isSatisfied(StudentInformation studentInformation) {
		return false;
	}

	@Override
	public DetailGraduationResult createDetailGraduationResult(StudentInformation studentInformation, Set<TakenLecture> takenLectures,
		Set<BasicAcademicalCulture> graduationLectures, int basicAcademicalCredit) {

		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			BASIC_ACADEMICAL_CULTURE.name(), basicAcademicalCredit);

		Set<Lecture> basicAcademicalLectures = convertToLectureSet(graduationLectures);

		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		Set<Lecture> taken = new HashSet<>();

		takenLectures.stream()
			.filter(takenLecture -> basicAcademicalLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});

		detailCategoryResult.calculate(taken, basicAcademicalLectures);
		takenLectures.removeAll(removedTakenLecture);

		return DetailGraduationResult.create(BASIC_ACADEMICAL_CULTURE, basicAcademicalCredit,
			List.of(detailCategoryResult));
	}
}
