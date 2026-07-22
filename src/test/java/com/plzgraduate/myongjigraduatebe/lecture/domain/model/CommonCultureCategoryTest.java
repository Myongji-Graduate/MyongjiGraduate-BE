package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommonCultureCategoryTest {

	@DisplayName("2025학번 이후에도 현행 공통교양 세부 카테고리를 적용한다.")
	@Test
	void containsCurrentEntryYear() {
		assertThat(CommonCultureCategory.CHRISTIAN_B.isContainsEntryYear(25)).isTrue();
		assertThat(CommonCultureCategory.EXPRESSION.isContainsEntryYear(25)).isTrue();
		assertThat(CommonCultureCategory.ENGLISH.isContainsEntryYear(25)).isTrue();
		assertThat(CommonCultureCategory.DIGITAL_LITERACY.isContainsEntryYear(25)).isTrue();
		assertThat(CommonCultureCategory.CAREER.isContainsEntryYear(25)).isFalse();
	}
}
