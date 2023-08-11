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

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DefaultBasicAcademicalManager implements BasicAcademicalManager {

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

		takenLectures.stream()
			.filter(takenLecture -> basicAcademicalLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});
		takenLectures.removeAll(removedTakenLecture);

		int normalLeftCredit = getNormalLeftCredit(taken, basicAcademicalCredit);
		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create(
			BASIC_ACADEMICAL_CULTURE.getName(), true, basicAcademicalCredit, normalLeftCredit, 0);
		detailCategoryResult.calculate(taken, basicAcademicalLectures);

		return DetailGraduationResult.create(BASIC_ACADEMICAL_CULTURE, basicAcademicalCredit,
			List.of(detailCategoryResult));
	}

	private int getNormalLeftCredit(Set<Lecture> taken, int categoryTotalCredit) {
		int totalTakenCredit = taken.stream()
			.mapToInt(Lecture::getCredit)
			.sum();
		int normalLeftCredit = totalTakenCredit - categoryTotalCredit;
		return Math.max(normalLeftCredit, 0);
	}
}
