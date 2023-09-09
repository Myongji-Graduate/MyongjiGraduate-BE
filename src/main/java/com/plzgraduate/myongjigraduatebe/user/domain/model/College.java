package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum College {

	HUMANITIES("인문대", List.of("국어국문", "문예창작", "영어영문", "중어중문", "일어일문", "문헌정보", "사학", "아랍지역", "사학", "철학")),
	SOCIAL_SCIENCE("사회과학대", List.of("행정", "경제", "정치외교", "디지털미디어", "아동", "청소년지도", "사회복지")),
	BUSINESS("경영대", List.of("경영", "경영정보", "국제통상")),
	LAW("법대", List.of("법학")),
	ICT("ICT융합대", List.of("디지털콘텐츠디자인", "응용소프트웨어", "데이터테크놀로지"));

	private final String text;
	private final List<String> holdingMajors;

	public static College findBelongingCollege(String major) {
		return Arrays.stream(College.values())
			.filter(college -> college.getHoldingMajors().contains(major))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));
	}

}
