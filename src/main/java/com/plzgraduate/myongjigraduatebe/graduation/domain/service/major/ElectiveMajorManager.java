package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ElectiveMajorManager {

	// 현장실습과목(haveToLectrue 제외)
	private static final List<String> PRACTICE_LECTURE_CODES = List.of(
		"KMR01801", "KMR01802", "KMR01804", "KMR01805", "KMR01851", "KMR01852", "HAH01371", "HFC01412",
		"HFM01404", "JDC01361", "JEE01356", "JEE01357", "JEH01493", "JEH01494", "JEI01430", "JEI01467", "JEJ02549",
		"JEJ02554", "JEJ02558", "JEJ02559", "JEJ02560", "JEJ02561", "KMD02902", "KMD02903", "KMR01551", "KMR01552",
		"KMR01553", "KMR01554", "KMR01555", "KMR01560", "KMR01561", "KMR01562", "KMR01563", "KMR01564", "KMR01566",
		"KMR01567", "KMR01703", "KMR01705", "KMR01710", "KMR01712", "KMR01803", "KMR01817"
	);
	private static final String ELECTIVE_MAJOR_NAME = "전공선택";

	public DetailCategoryResult createDetailCategoryResult(
		TakenLectureInventory takenLectureInventory,
		Set<Lecture> electiveLectures, int electiveMajorTotalCredit
	) {
		Set<Lecture> takenElective = new HashSet<>();
		Set<TakenLecture> finishedTakenLecture = new HashSet<>();
		takenLectureInventory.getTakenLectures()
			.stream()
			.filter(takenLecture -> electiveLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				finishedTakenLecture.add(takenLecture);
				takenElective.add(takenLecture.getLecture());
			});
		DetailCategoryResult electiveMajorResult = DetailCategoryResult.create(ELECTIVE_MAJOR_NAME,
			true, electiveMajorTotalCredit
		);
		excludePracticeLectureForHaveToLecture(electiveLectures);
		electiveMajorResult.calculate(takenElective, electiveLectures);
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);
		return electiveMajorResult;
	}

	private void excludePracticeLectureForHaveToLecture(Set<Lecture> electiveLectures) {
		electiveLectures.removeIf(lecture -> PRACTICE_LECTURE_CODES.contains(lecture.getId()));
	}
}
