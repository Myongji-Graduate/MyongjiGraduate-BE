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

/**
 * N택 M의 선택과목이 있는 경우 처리하는 핸들러 클래스
 * 국제통상의 경우 회계원리, 마케팅원론, 재무관리 원론, 인적자원관리, 생산운영관리(폐지), 운영관리(구, 생산운영관리) 5개 중 4개 선택
 * 경영정보의 경우 인적자원관리, 마켓팅원론, 재무관리원론에서 ~18학번까지는 3개 모두 이수, 19학번 이후 택 2
 * 경영의 경우 국제통상원론, 국제경양, 경영정보 중 택1
 **/
public class OptionalMandatoryMajoHandler implements MandatoryMajorSpecialCaseHandler {

	private static final String MANAGEMENT_INFORMATION = "경영정보학과";
	private static final String BUSINESS = "경영학과";
	private static final String ADMINISTRATIONS = "행정학과";
	private static final String INTERNATIONAL_TRADE = "국제통상학과";
	private static final int CLASS_OF_17 = 17;
	private static final int CLASS_OF_19 = 19;

	public boolean isSupport(User user, MajorGraduationCategory majorGraduationCategory) {
		String calculatingMajor = getCalculatingMajor(user, majorGraduationCategory);
		if (calculatingMajor.equals(MANAGEMENT_INFORMATION) && user.getEntryYear() >= CLASS_OF_19) {
			return true;
		}
		if (calculatingMajor.equals(ADMINISTRATIONS) && user.getEntryYear() >= CLASS_OF_17) {
			return true;
		}
		return List.of(BUSINESS, INTERNATIONAL_TRADE).contains(calculatingMajor);
	}

	@Override
	public MandatorySpecialCaseInformation getMandatorySpecialCaseInformation(User user,
		MajorGraduationCategory majorGraduationCategory, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures) {
		OptionalMandatory optionalMandatory = OptionalMandatory.from(getCalculatingMajor(user, majorGraduationCategory));
		int removedMandatoryTotalCredit = 0;
		boolean isCompletedMandatorySpecialCase = checkCompleteOptionalMandatory(takenLectureInventory, mandatoryLectures,
			electiveLectures, optionalMandatory);
		if (!isCompletedMandatorySpecialCase ) {
			removedMandatoryTotalCredit = optionalMandatory.getTotalOptionalMandatoryCredit()
				- optionalMandatory.getChooseLectureCredit();
		}
		return MandatorySpecialCaseInformation.of(isCompletedMandatorySpecialCase , removedMandatoryTotalCredit);
	}

	private boolean checkCompleteOptionalMandatory(TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures, OptionalMandatory optionalMandatory) {
		int chooseNum = optionalMandatory.getChooseNumber();
		/*
		 * 전공과목 Set 에서 전공선택필수과목에 해당되는 과목들을 추출한다.
		 */
		Set<Lecture> optionalMandatoryLectures = mandatoryLectures.stream().filter(
			optionalMandatory.getOptionalMandatoryLectures()::contains).collect(Collectors.toSet());

		/*
		remainingMandatoryLectures에 모든 전공선택필수 과목을 넣고 수강했을 시 제거한다.
		최종적으로 전공선택필수 과목들 목록에서 사용자가 전공선택필수과목을 수강했다면 제거한다.
		최종적으로  remainingMandatoryLectures에 남아있는 과목은 수강해야하는 과목들이다.
		N택 M개 수강이라면 M개 이상 수강하였으면 전공필수에서 전부 제거, 전공 선택으로 이관하고 아니라면 전공필수에 그대로 남아있어야한다.
		 */
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
