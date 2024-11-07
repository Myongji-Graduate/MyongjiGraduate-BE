package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MajorTypeTest {

	private static Stream<Arguments> provideGraduationCategoryForCreatingMajorType() {
		return Stream.of(
			Arguments.of(GraduationCategory.PRIMARY_MANDATORY_MAJOR, MajorType.PRIMARY),
			Arguments.of(GraduationCategory.PRIMARY_ELECTIVE_MAJOR, MajorType.PRIMARY),
			Arguments.of(GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE, MajorType.PRIMARY),
			Arguments.of(GraduationCategory.DUAL_MANDATORY_MAJOR, MajorType.DUAL),
			Arguments.of(GraduationCategory.DUAL_ELECTIVE_MAJOR, MajorType.DUAL),
			Arguments.of(GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE, MajorType.DUAL),
			Arguments.of(GraduationCategory.SUB_MAJOR, MajorType.SUB)
		);
	}

	@DisplayName("GraduationCategory별로 올바른 MajorType이 반환되는지 확인한다.")
	@MethodSource("provideGraduationCategoryForCreatingMajorType")
	@ParameterizedTest
	void getMajorByGraduationCategory(GraduationCategory graduationCategory, MajorType majorType) {

		//when
		MajorType result = MajorType.from(graduationCategory);

		//then
		assertThat(result).isEqualTo(majorType);
	}
}
