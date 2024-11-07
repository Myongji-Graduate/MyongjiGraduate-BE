package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum College {

	HUMANITIES("인문대",
		List.of("국어국문학과", "문예창작학과", "영어영문학과", "중어중문학과", "일어일문학과", "문헌정보학과", "미술사학과", "아랍지역학과",
			"사학과", "철학과")),
	SOCIAL_SCIENCE("사회과학대", List.of("행정학과", "경제학과", "정치외교학과", "디지털미디어학과", "아동학과", "청소년지도학과")),
	BUSINESS("경영대", List.of("경영학과", "경영정보학과", "국제통상학과")),
	LAW("법대", List.of("법학과")),
	ICT("ICT융합대", List.of("디지털콘텐츠디자인학과", "응용소프트웨어전공", "데이터테크놀로지전공"));

	private final String name;
	private final List<String> holdingMajors;

	public static College findBelongingCollege(String major) {
		return Arrays.stream(College.values())
			.filter(college -> college.getHoldingMajors()
				.contains(major))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));
	}

}
