package com.plzgraduate.myongjigraduatebe.graduation.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCommonCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class DefaultDetailGraduationResolverTest {

	@Mock
	private CalculateCommonCultureGraduationUseCase calculateCommonCultureGraduationUseCase;
	@InjectMocks
	private DefaultDetailGraduationResolver defaultDetailGraduationResolver;

	@DisplayName("공통교양 졸업 카테고리를 계산할 수 있는 UseCase와 해당 카테고리의 TotalCredit을 반환한다.")
	@Test
	void resolveDetailGraduationUseCase() {
	    //given
		User user = User.builder()
			.entryYear(19)
			.primaryMajor("응용소프트웨어전공").build();
		GraduationCategory commonCultureGraduationCategory = GraduationCategory.COMMON_CULTURE;
		//when
		Map<CalculateDetailGraduationUseCase, Integer> resolvedDetailGraduation = defaultDetailGraduationResolver.resolveDetailGraduationUseCase(
			user, commonCultureGraduationCategory);

		//then
		assertThat(resolvedDetailGraduation.keySet()).contains(calculateCommonCultureGraduationUseCase);
		assertThat(resolvedDetailGraduation.get(calculateCommonCultureGraduationUseCase)).isEqualTo(17);
	}
}
