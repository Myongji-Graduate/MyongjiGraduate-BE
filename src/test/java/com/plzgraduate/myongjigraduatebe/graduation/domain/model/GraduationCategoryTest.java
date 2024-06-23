package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GraduationCategoryTest {

	@DisplayName("전공 관련 GraduationCategory인 경우 전공 필수인지 선택인지 확인한다.")
	@CsvSource({"PRIMARY_MANDATORY_MAJOR, true", "DUAL_MANDATORY_MAJOR, true", "PRIMARY_ELECTIVE_MAJOR, false"})
	@ParameterizedTest
	void checkMandatoryOrElective(GraduationCategory graduationCategory, boolean isMandatory) {
		//when
		boolean result = graduationCategory.checkMandatoryIfSeperatedByMandatoryAndElective();

		//then
		assertThat(result).isEqualTo(isMandatory);
	}
}
