package com.plzgraduate.myongjigraduatebe.graduation.domain.service.submajor;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SubMajorGraduationManager implements GraduationManager<MajorLecture> {

	@Override
	public DetailGraduationResult createDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory,
		Set<MajorLecture> graduationLectures, int graduationResultTotalCredit) {
		Set<TakenLecture> removedTakenLecture = new HashSet<>();
		DetailCategoryResult detailCategoryResult = generateDetailCategoryResult(
			takenLectureInventory,
			graduationLectures, removedTakenLecture, graduationResultTotalCredit);

		takenLectureInventory.handleFinishedTakenLectures(removedTakenLecture);

		return DetailGraduationResult.create(GraduationCategory.SUB_MAJOR,
			graduationResultTotalCredit,
			List.of(detailCategoryResult));
	}

	private DetailCategoryResult generateDetailCategoryResult(
		TakenLectureInventory takenLectureInventory,
		Set<MajorLecture> graduationLectures, Set<TakenLecture> removedTakenLecture,
		int graduationResultTotalCredit) {
		boolean isSatisfiedMandatory = true;
		Set<Lecture> subMajorGraduationLectures = graduationLectures.stream()
			.map(MajorLecture::getLecture)
			.collect(Collectors.toSet());
		Set<Lecture> taken = new HashSet<>();

		takenLectureInventory.getTakenLectures()
			.stream()
			.filter(takenLecture -> subMajorGraduationLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				removedTakenLecture.add(takenLecture);
				taken.add(takenLecture.getLecture());
			});

		DetailCategoryResult detailCategoryResult = DetailCategoryResult.create("전공선택",
			isSatisfiedMandatory,
			graduationResultTotalCredit);
		detailCategoryResult.calculate(taken, subMajorGraduationLectures);
		return detailCategoryResult;
	}
}
