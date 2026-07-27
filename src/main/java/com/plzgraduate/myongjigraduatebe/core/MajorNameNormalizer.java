package com.plzgraduate.myongjigraduatebe.core;

import java.util.List;

public final class MajorNameNormalizer {

	private MajorNameNormalizer() {
	}

	public static List<String> resolveLookupMajors(String major) {
		if (major == null || major.isBlank()) {
			return List.of();
		}

		if ("국제통상학전공".equals(major)) {
			return List.of("국제통상학전공", "국제통상학과");
		}

		if ("국제통상학과".equals(major)) {
			return List.of("국제통상학과", "국제통상학전공");
		}

		return List.of(major);
	}
}
