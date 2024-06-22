package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MandatoryMajorManager {

	private static final String MANDATORY_MAJOR_NAME = "전공필수";

	private final List<MandatoryMajorSpecialCaseHandler> mandatoryMajorSpecialCaseHandlers;

	public DetailCategoryResult createDetailCategoryResult(User user, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures,
		MajorType majorType) {
		Set<Lecture> takenMandatory = new HashSet<>();
		Set<TakenLecture> finishedTakenLecture = new HashSet<>();
		boolean isSatisfiedMandatory = true;
		int removeMandatoryTotalCredit = 0;

		for (MandatoryMajorSpecialCaseHandler mandatoryMajorSpecialCaseHandler : mandatoryMajorSpecialCaseHandlers) {
			if (mandatoryMajorSpecialCaseHandler.isSupport(user, majorType)) {
				MandatorySpecialCaseInformation mandatorySpecialCaseInformation = mandatoryMajorSpecialCaseHandler.getMandatorySpecialCaseInformation(
					user, majorType, takenLectureInventory, mandatoryLectures, electiveLectures);
				isSatisfiedMandatory = mandatorySpecialCaseInformation.isCompleteMandatorySpecialCase();
				removeMandatoryTotalCredit = mandatorySpecialCaseInformation.getRemovedMandatoryTotalCredit();
			}
		}

		takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> mandatoryLectures.contains(takenLecture.getLecture()))
			.forEach(takenLecture -> {
				finishedTakenLecture.add(takenLecture);
				takenMandatory.add(takenLecture.getLecture());
			});
		DetailCategoryResult majorMandatoryResult = DetailCategoryResult.create(MANDATORY_MAJOR_NAME, isSatisfiedMandatory,
			calculateTotalCredit(takenMandatory, mandatoryLectures, removeMandatoryTotalCredit));
		majorMandatoryResult.calculate(takenMandatory, mandatoryLectures);
		takenLectureInventory.handleFinishedTakenLectures(finishedTakenLecture);
		return majorMandatoryResult;
	}

	private int calculateTotalCredit(Set<Lecture> taken, Set<Lecture> mandatoryLectures, int removedCredit) {
		int totalCredit = 0;
		for (Lecture lecture : mandatoryLectures) {
			if (!taken.contains(lecture) && lecture.getIsRevoked() == 1) {
				continue;
			}
			totalCredit += lecture.getCredit();
		}
		return totalCredit - removedCredit;
	}

}
