package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum College {

	HUMANITIES(
		"인문대",
		List.of("국어국문학전공", "문예창작학과", "영어영문학전공", "중어중문학전공", "일어일문학전공",
			"문헌정보학전공", "미술사학과", "아랍지역학전공", "사학과", "철학과"), 16, 24),
	SOCIAL_SCIENCE("사회과학대", List.of("행정학전공", "경제학전공", "정치외교학전공", "디지털미디어학부", "아동학전공", "청소년지도학전공"),15, 24),
	BUSINESS("경영대", List.of("경영학전공", "경영정보학과", "국제통상학전공"),16, 24),
	LAW("법대", List.of("법학과"), 16, 24),
	ICT("ICT융합대", List.of("디지털콘텐츠디자인학과", "응용소프트웨어전공", "데이터사이언스전공"),16, 24),
	HUMANITIES_NEW("인문대", List.of("국어국문학전공", "영어영문학전공", "미술사·역사학전공", "문헌정보학전공", "중어중문학전공",
			"일어일문학전공", "아랍지역학전공", "문예창작학과"),25, 99),
	SOCIAL_SCIENCE_NEW("사회과학대", List.of("행정학전공", "정치외교학전공", "경제학전공",
			"국제통상학전공", "응용통계학전공", "법학과"), 25, 99),
	MEDIA_HUMAN_LIFE("미디어·휴먼라이프", List.of("디지털미디어학부", "청소년지도학전공", "아동학전공"), 25, 99),
	BUSINESS_NEW("경영대", List.of("경영학전공", "글로벌비즈니스학전공", "경영정보학과"), 25, 99),
	ARTIFICIAL_INTELLIGENCE_SOFTWARE("인공지능·소프트웨어융합대학", List.of("응용소프트웨어전공", "데이터사이언스전공", "인공지능전공", "디지털콘텐츠디자인학과"), 25, 99);

	private final String name;
	private final List<String> holdingMajors;
	private final int startYear;
	private final int endYear;

	public static College findBelongingCollege(String major, int entryYear) {
		return Arrays.stream(College.values())
			.filter(college -> college.getHoldingMajors()
				.contains(major))
				.filter(college -> entryYear >= college.getStartYear() && entryYear <= college.getEndYear())
				.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));
	}
}
