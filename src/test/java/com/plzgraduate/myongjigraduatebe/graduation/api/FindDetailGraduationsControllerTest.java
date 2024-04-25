package com.plzgraduate.myongjigraduatebe.graduation.api;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;

class FindDetailGraduationsControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("공통교양 졸업 상세 결과를 조회한다.")
	@Test
	void getCommonDetailGraduations() throws Exception {
		//given
		List<DetailCategoryResult> detailCategories = List.of(
			DetailCategoryResult.builder().build(),
			DetailCategoryResult.builder().build(),
			DetailCategoryResult.builder().build(),
			DetailCategoryResult.builder().build()
		);
		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.graduationCategory(COMMON_CULTURE)
			.totalCredit(17)
			.takenCredit(17)
			.isCompleted(true)
			.detailCategory(detailCategories)
			.build();

		given(calculateSingleDetailGraduationUseCase.calculateSingleDetailGraduation(1L,
			COMMON_CULTURE)).willReturn(detailGraduationResult);

		//when //then
		mockMvc.perform(get("/api/v1/graduations/detail")
				.param("graduationCategory", "COMMON_CULTURE"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalCredit").value(17))
			.andExpect(jsonPath("$.takenCredit").value(17))
			.andExpect(jsonPath("$.completed").value(true))
			.andExpect(jsonPath("$.detailCategory.length()").value(detailCategories.size()));
	}

	@WithMockAuthenticationUser
	@DisplayName("GraduationCategory에 해당하지 않는 졸업 상세 조회 시 에러를 반환한다.")
	@Test
	void getDetailGraduationsWithInvalidGraduationCategory() throws Exception {
		//given
		String invalidGraduationCategoryName = "COMMON_CULTUR";

		//when //then
		mockMvc.perform(get("/api/v1/graduations/detail")
				.param("graduationCategory", invalidGraduationCategoryName))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
			.andExpect(jsonPath("$.message").value("Failed to convert value: " + invalidGraduationCategoryName));
	}

}
