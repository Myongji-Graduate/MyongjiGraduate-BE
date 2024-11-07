package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 철학과의 경우 답사1, 답사2는 폐지 되었지만 2021번 이전까지 전공필수 '신유학의 이해' '유학사상의이해' 중 택1 이수 시 대체 인정
 */

@Component
public class ReplaceMandatoryMajorHandler implements MandatoryMajorSpecialCaseHandler {

	private static final List<Lecture> REPLACED_LECTURES = List.of(
		Lecture.of("HAI01110", "답사1", 1, 1, null),
		Lecture.of("HAI01111", "답사2", 1, 1, "HAI01111")
	);
	private static final List<Lecture> REPLACING_LECTURES = List.of(
		Lecture.of("HAI01348", "신유학의이해", 3, 0, null),
		Lecture.of("HAI01247", "유학사상의이해", 3, 0, null)
	);

	@Override
	public boolean isSupport(User user, MajorType majorType) {
		String major = user.getMajorByMajorType(majorType);
		return major.equals("철학과") && user.getEntryYear() <= 21;
	}

	@Override
	public MandatorySpecialCaseInformation getMandatorySpecialCaseInformation(User user,
		MajorType majorType, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		boolean completeMandatorySpecialCase = checkCompleteReplaceMandatory(takenLectureInventory,
			mandatoryLectures,
			electiveLectures);
		int removedMandatoryTotalCredit = 0;
		if (!completeMandatorySpecialCase) {
			removedMandatoryTotalCredit = 3;
		}
		return MandatorySpecialCaseInformation.of(completeMandatorySpecialCase,
			removedMandatoryTotalCredit);

	}

	public boolean checkCompleteReplaceMandatory(TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		if (checkTakeReplacedLectures(takenLectureInventory)) {
			return true;
		}
		List<TakenLecture> replacingLectures = filterTakenLecturesContainsReplacingLectures(
			takenLectureInventory);
		if (!replacingLectures.isEmpty()) {
			mandatoryLectures.add(replacingLectures.get(0)
				.getLecture());
			electiveLectures.remove(replacingLectures.get(0)
				.getLecture());
			return true;
		}
		mandatoryLectures.addAll(REPLACING_LECTURES);
		REPLACING_LECTURES.forEach(electiveLectures::remove);
		return false;
	}

	private boolean checkTakeReplacedLectures(TakenLectureInventory takenLectureInventory) {
		long replacedTaken = takenLectureInventory.getTakenLectures()
			.stream()
			.map(TakenLecture::getLecture)
			.filter(REPLACED_LECTURES::contains)
			.count();
		return replacedTaken == REPLACED_LECTURES.size();
	}

	private List<TakenLecture> filterTakenLecturesContainsReplacingLectures(
		TakenLectureInventory takenLectureInventory) {
		return takenLectureInventory.getTakenLectures()
			.stream()
			.filter(takenLecture -> REPLACING_LECTURES.contains(takenLecture.getLecture()))
			.sorted(Comparator
				.comparingInt(TakenLecture::getYear)
				.thenComparing(TakenLecture::getSemester))
			.collect(Collectors.toList());
	}
}
