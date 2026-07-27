package com.plzgraduate.myongjigraduatebe.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MajorNameNormalizerTest {

	@DisplayName("국제통상학전공은 조회 시 국제통상학과도 함께 찾는다.")
	@Test
	void resolveLookupMajors() {
		assertThat(MajorNameNormalizer.resolveLookupMajors("국제통상학전공"))
			.containsExactly("국제통상학전공", "국제통상학과");
	}
}
