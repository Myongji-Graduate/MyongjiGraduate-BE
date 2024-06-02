package com.plzgraduate.myongjigraduatebe.graduation.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.support.resolver.SingleCalculateDetailGraduationUseCaseResolver;

@SpringBootTest
@ActiveProfiles("test")
class SingleCalculateDetailGraduationUseCaseResolverTest {

	@Autowired
	private SingleCalculateDetailGraduationUseCaseResolver singleCalculateDetailGraduationUseCaseResolver;

	@DisplayName("졸업 카테고리를 계산할 수 있는 CalculateDetailGraduationUseCaseResolver 반환한다.")
	@ValueSource(strings =
		{"COMMON_CULTURE", "CORE_CULTURE", "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR", "DUAL_MANDATORY_MAJOR",
			"DUAL_ELECTIVE_MAJOR", "SUB_MAJOR", "PRIMARY_BASIC_ACADEMICAL_CULTURE", "DUAL_BASIC_ACADEMICAL_CULTURE"
		})
	@ParameterizedTest
	void resolveCalculateDetailGraduationUseCase(String graduationCategoryName) {
		//given
		GraduationCategory graduationCategory = GraduationCategory.valueOf(graduationCategoryName);

		// when
		CalculateDetailGraduationUseCase calculateDetailGraduationUseCase = singleCalculateDetailGraduationUseCaseResolver.resolveCalculateDetailGraduationUseCase(
			graduationCategory);

		//then
		assertThat(calculateDetailGraduationUseCase.supports(graduationCategory)).isEqualTo(true);
	}
}
