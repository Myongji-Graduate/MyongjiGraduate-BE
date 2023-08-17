package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;

public class ReplaceMandatoryMajorHandler implements MajorExceptionHandler {
	private int removedMandatoryTotalCredit = 0;
	private static final List<Lecture> REPLACED_LECTURES = List.of(
		Lecture.of("HAI01110", "답사1", 1, 1, null),
		Lecture.of("HAI01111", "답사2", 1, 1, "HAI01111")
	);
	private static final List<Lecture> REPLACING_LECTURES = List.of(
		Lecture.of("HAI01348", "신유학의이해", 3, 0, null),
		Lecture.of("HAI01247", "유학사상의이해", 3, 0, null)
	);

	@Override
	public boolean isSupport(StudentInformation studentInformation) {
		return studentInformation.getDepartment().equals("철학과") && studentInformation.getEntryYear() <= 21;
	}

	@Override
	public boolean checkMandatoryCondition(StudentInformation studentInformation,
		TakenLectureInventory takenLectureInventory, Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		boolean checkCondition = checkCompleteReplaceMandatory(takenLectureInventory, mandatoryLectures,
			electiveLectures);
		if (!checkCondition) {
			removedMandatoryTotalCredit = 3;
		}
		return checkCondition;
	}

	@Override
	public int getRemovedMandatoryTotalCredit() {
		return removedMandatoryTotalCredit;
	}

	public boolean checkCompleteReplaceMandatory(TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		if (checkTakeReplacedLectures(takenLectureInventory)) {
			return true;
		}
		List<TakenLecture> replacingLectures = filterTakenLecturesContainsReplacingLectures(takenLectureInventory);
		if (!replacingLectures.isEmpty()) {
			mandatoryLectures.add(replacingLectures.get(0).getLecture());
			electiveLectures.remove(replacingLectures.get(0).getLecture());
			return true;
		}
		mandatoryLectures.addAll(REPLACING_LECTURES);
		REPLACING_LECTURES.forEach(electiveLectures::remove);
		return false;
	}

	private boolean checkTakeReplacedLectures(TakenLectureInventory takenLectureInventory) {
		long replacedTaken = takenLectureInventory.getTakenLectures().stream()
			.map(TakenLecture::getLecture)
			.filter(REPLACED_LECTURES::contains).count();
		return replacedTaken == REPLACED_LECTURES.size();
	}

	private List<TakenLecture> filterTakenLecturesContainsReplacingLectures(
		TakenLectureInventory takenLectureInventory) {
		return takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> REPLACING_LECTURES.contains(takenLecture.getLecture()))
			.sorted(Comparator
				.comparingInt(TakenLecture::getYear)
				.thenComparing(TakenLecture::getSemester))
			.collect(Collectors.toList());
	}
}
