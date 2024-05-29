package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationCategory.DUAL;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationCategory.PRIMARY;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorGraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public class OptionalMandatoryHandler implements MajorExceptionHandler {

	private static final String MANAGEMENT_INFORMATION = "경영정보학과";
	private static final String BUSINESS = "경영학과";
	private static final String ADMINISTRATIONS = "행정학과";
	private static final String INTERNATIONAL_TRADE = "국제통상학과";
	private static final int CLASS_OF_17 = 17;
	private static final int CLASS_OF_19 = 19;
	private int removedMandatoryTotalCredit = 0;
	private OptionalMandatory optionalMandatory;

	public boolean isSupport(User user, MajorGraduationCategory majorGraduationCategory) {
		String calculatingMajor = getCalculatingMajor(user, majorGraduationCategory);
		if (calculatingMajor.equals(MANAGEMENT_INFORMATION) && user.getEntryYear() >= CLASS_OF_19) {
			this.optionalMandatory = OptionalMandatory.from(getCalculatingMajor(user, majorGraduationCategory));
			return true;
		}
		if (calculatingMajor.equals(ADMINISTRATIONS) && user.getEntryYear() >= CLASS_OF_17) {
			this.optionalMandatory = OptionalMandatory.from(getCalculatingMajor(user, majorGraduationCategory));
			return true;
		}
		if (List.of(BUSINESS, INTERNATIONAL_TRADE).contains(calculatingMajor)) {
			this.optionalMandatory = OptionalMandatory.from(getCalculatingMajor(user, majorGraduationCategory));
			return true;
		}
		return false;
	}

	@Override
	public boolean checkMandatoryCondition(TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		boolean checkMandatoryCondition = checkCompleteOptionalMandatory(takenLectureInventory, mandatoryLectures,
			electiveLectures);

		if (!checkMandatoryCondition) {
			removedMandatoryTotalCredit = optionalMandatory.getTotalOptionalMandatoryCredit(optionalMandatory)
				- optionalMandatory.getChooseLectureCredit(optionalMandatory);
		}
		return checkMandatoryCondition;
	}

	@Override
	public int getRemovedMandatoryTotalCredit() {
		return removedMandatoryTotalCredit;
	}

	private boolean checkCompleteOptionalMandatory(TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		int chooseNum = optionalMandatory.getChooseNumber();
		//전공과목Set에서 전공필수과목에 해당되는 과목들을 추출한다.
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

	private String getCalculatingMajor(User user, MajorGraduationCategory majorGraduationCategory) {
		if (majorGraduationCategory == PRIMARY) {
			return user.getPrimaryMajor();
		}
		if (majorGraduationCategory == DUAL) {
			return user.getDualMajor();
		}
		return user.getSubMajor();
	}
}
