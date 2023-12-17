package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import java.util.HashSet;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

public class ElectiveMajorManager {
	public DetailCategoryResult createDetailCategoryResult(TakenLectureInventory takenLectureInventory,
		Set<Lecture> electiveLectures, int electiveMajorTotalCredit) {
		Set<Lecture> takenElective = new HashSet<>();
		Set<TakenLecture> finishedTakenLecture = new HashSet<>();
		takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> electiveLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				finishedTakenLecture.add(takenLecture);
				takenElective.add(takenLecture.getLecture());
			});
		DetailCategoryResult electiveMajorResult = DetailCategoryResult.create("전공선택", true, electiveMajorTotalCredit);
		electiveMajorResult.calculate(takenElective, electiveLectures);
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);

		return electiveMajorResult;
	}
}
