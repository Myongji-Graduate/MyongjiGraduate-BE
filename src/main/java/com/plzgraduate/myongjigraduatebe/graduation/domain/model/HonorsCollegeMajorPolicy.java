package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class HonorsCollegeMajorPolicy {

	public static final String M_FRESHMAN_SEMINAR = "KMR02638";

	private static final Map<String, String> MAJOR_TO_EXPLORATION_SEMINAR = Map.ofEntries(
		Map.entry("국어국문학전공", "HAZ00101"),
		Map.entry("문예창작학과", "HAZ00101"),
		Map.entry("문헌정보학전공", "HAZ00101"),
		Map.entry("미술사·역사학전공", "HAZ00101"),
		Map.entry("영어영문학전공", "HAZ00102"),
		Map.entry("중어중문학전공", "HAZ00102"),
		Map.entry("일어일문학전공", "HAZ00102"),
		Map.entry("아랍지역학전공", "HAZ00102"),
		Map.entry("경제학전공", "HBZ01102"),
		Map.entry("국제통상학전공", "HBZ01102"),
		Map.entry("응용통계학전공", "HBZ01102"),
		Map.entry("경영학전공", "HBY01106"),
		Map.entry("경영정보학과", "HBY01106"),
		Map.entry("행정학전공", "HBZ01103"),
		Map.entry("정치외교학전공", "HBZ01103"),
		Map.entry("법학과", "HBZ01103"),
		Map.entry("디지털미디어학부", "HBS01101"),
		Map.entry("청소년지도학전공", "HBS01101"),
		Map.entry("아동학전공", "HBS01101"),
		Map.entry("응용소프트웨어전공", "HEF01103"),
		Map.entry("데이터사이언스전공", "HEF01103"),
		Map.entry("인공지능전공", "HEF01103"),
		Map.entry("디지털콘텐츠디자인학과", "HEF01103")
	);

	private HonorsCollegeMajorPolicy() {
	}

	public static boolean requiresMFreshmanSeminar(User user) {
		return user.isHonorsCollege() && user.getEntryYear() >= 25;
	}

	public static boolean isSupportedTargetMajor(String major) {
		return MAJOR_TO_EXPLORATION_SEMINAR.containsKey(major);
	}

	public static List<String> supportedTargetMajors() {
		return MAJOR_TO_EXPLORATION_SEMINAR.keySet().stream().sorted().toList();
	}

	public static Optional<String> findMandatoryExplorationSeminar(User user, MajorType majorType) {
		if (!requiresMFreshmanSeminar(user) || majorType != MajorType.PRIMARY) {
			return Optional.empty();
		}
		return Optional.ofNullable(MAJOR_TO_EXPLORATION_SEMINAR.get(user.getPrimaryMajor()));
	}
}
