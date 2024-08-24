package com.plzgraduate.myongjigraduatebe.user.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

class StudentCategoryTest {

	@DisplayName("학생 이수구분 카테고리에 포함되는 졸업 카테고리인지 검증한다.")
	@ParameterizedTest
	@ValueSource(strings = {"DUAL_MANDATORY_MAJOR", "DUAL_ELECTIVE_MAJOR", "SUB_MAJOR",
		"DUAL_BASIC_ACADEMICAL_CULTURE"})
	void validateGraduationCategoryInclusion(String graduationCategoryName) {
		//given
		User user = User.builder()
			.studentCategory(StudentCategory.NORMAL).build();
		GraduationCategory graduationCategory = GraduationCategory.valueOf(graduationCategoryName);

		//when //then
		assertThatThrownBy(
				() -> user.getStudentCategory().validateGraduationCategoryInclusion(graduationCategory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage(ErrorCode.UNFITTED_GRADUATION_CATEGORY.toString());

	}

}
