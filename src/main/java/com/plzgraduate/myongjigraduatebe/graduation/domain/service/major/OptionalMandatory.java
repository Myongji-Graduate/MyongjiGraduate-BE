package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OptionalMandatory {
	ADMINISTRATIONS("행정학전공", 2, Set.of(
		Lecture.of("HBA01109", "행정학의이해", 3, 0, null),
		Lecture.of("HBA01110", "리더십과조직행동", 3, 0, null),
		Lecture.of("HBA01111", "감성과현대행정", 3, 0, null),
		Lecture.of("HBA01112", "비교정책론", 3, 0, null),
		Lecture.of("HBA01113", "비영리조직", 3, 0, null),
		Lecture.of("HBA01222", "커뮤니티개발연구", 3, 0, null)
	)),

	BUSINESS("경영학전공", 1, Set.of(
		Lecture.of("HBX01128", "국제통상원론", 3, 0, null),
		Lecture.of("HBX01127", "국제경영학", 3, 0, null),
		Lecture.of("HBX01125", "경영정보", 3, 0, null),
		Lecture.of("HBY01103", "경영정보", 3, 1, null)
	)),
	INTERNATIONAL_TRADE("국제통상학전공", 4, Set.of(
			Lecture.of("HBX01104", "회계원리", 3, 0, null),
			Lecture.of("HBX01113", "인적자원관리", 3, 0, null),
			Lecture.of("HBX01106", "마케팅원론", 3, 0, null),
			Lecture.of("HBX01105", "재무관리원론", 3, 1, null),
			Lecture.of("HBX01147", "재무관리", 3, 0, null),
			Lecture.of("HBX01114", "생산운영관리", 3, 1, null),
			Lecture.of("HBX01143", "운영관리", 3, 0, null)
	)),
	MANAGEMENT_INFORMATION("경영정보학과", 2, Set.of(
			Lecture.of("HBX01113", "인적자원관리", 3, 0, null),
			Lecture.of("HBX01106", "마케팅원론", 3, 0, null),
			Lecture.of("HBX01105", "재무관리원론", 3, 1, null),
			Lecture.of("HBX01147", "재무관리", 3, 0, null)
	));

	private final String department;
	private final int chooseNumber;
	private final Set<Lecture> optionalMandatoryLectures;

	public static OptionalMandatory from(String major) {
		return Arrays.stream(OptionalMandatory.values())
			.filter(optionalMandatory -> Objects.equals(optionalMandatory.getDepartment(), major))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 전공선택필수를 찾을 수 없습니다."));
	}

	public int getTotalOptionalMandatoryCredit() {
		return optionalMandatoryLectures.stream()
			.filter(lecture -> lecture.getIsRevoked() == 0)
			.mapToInt(Lecture::getCredit)
			.sum();
	}

	public int getChooseLectureCredit() {
		return chooseNumber * 3;
	}

}
