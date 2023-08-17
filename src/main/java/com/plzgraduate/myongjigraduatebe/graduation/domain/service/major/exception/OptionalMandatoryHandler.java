package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

public class OptionalMandatoryHandler implements MajorExceptionHandler {
	private int removedMandatoryTotalCredit = 0;

	public boolean isSupport(StudentInformation studentInformation) {
		if (studentInformation.getDepartment().equals("경영정보학과") && studentInformation.getEntryYear() >= 19) {
			return true;
		}
		return List.of("행정학과", "경영학과", "국제통상학과").contains(studentInformation.getDepartment());
	}

	@Override
	public boolean checkMandatoryCondition(StudentInformation studentInformation,
		TakenLectureInventory takenLectureInventory, Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		boolean checkMandatoryCondition = checkCompleteOptionalMandatory(studentInformation, takenLectureInventory,
			mandatoryLectures,
			electiveLectures);

		if (!checkMandatoryCondition) {
			OptionalMandatory optionalMandatory = OptionalMandatory.from(studentInformation);
			removedMandatoryTotalCredit = optionalMandatory.getTotalOptionalMandatoryCredit(optionalMandatory)
				- optionalMandatory.getChooseLectureCredit(optionalMandatory);
		}
		return checkMandatoryCondition;
	}

	@Override
	public int getRemovedMandatoryTotalCredit() {
		return removedMandatoryTotalCredit;
	}

	public boolean checkCompleteOptionalMandatory(StudentInformation studentInformation,
		TakenLectureInventory takenLectureInventory, Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		OptionalMandatory optionalMandatory = OptionalMandatory.from(studentInformation);
		int chooseNum = optionalMandatory.getChooseNUmber();
		Set<Lecture> optionalMandatoryLectures = mandatoryLectures.stream().filter(
			optionalMandatory.getOptionalMandatoryLectures()::contains).collect(Collectors.toSet());
		Set<Lecture> remainingMandatoryLectures = new HashSet<>(optionalMandatoryLectures);
		int count = 0;
		for (TakenLecture takenLecture : takenLectureInventory.getTakenLectures()) {
			if (optionalMandatoryLectures.contains(takenLecture.getLecture()) && count < chooseNum) {
				count++;
				remainingMandatoryLectures.remove(takenLecture.getLecture());
			}
		}
		if (count >= chooseNum) {
			electiveLectures.addAll(remainingMandatoryLectures);
			mandatoryLectures.removeAll(remainingMandatoryLectures);
		}
		return count >= chooseNum;
	}
}
