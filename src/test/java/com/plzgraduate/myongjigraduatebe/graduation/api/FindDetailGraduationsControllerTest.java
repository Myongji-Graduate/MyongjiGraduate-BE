package com.plzgraduate.myongjigraduatebe.graduation.api;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INVALIDATED_GRADUATION_CATEGORY;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CORE_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindDetailGraduationsControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("공통교양 졸업 상세 결과를 조회한다.")
	@Test
	void getCommonDetailGraduations() throws Exception {
		//given
		List<DetailCategoryResult> detailCategories = List.of(
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build()
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
	@DisplayName("핵심교양 졸업 상세 결과를 조회한다.")
	@Test
	void getCoreDetailGraduations() throws Exception {
		//given
		List<DetailCategoryResult> detailCategories = List.of(
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build()
		);
		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.graduationCategory(CORE_CULTURE)
			.totalCredit(12)
			.takenCredit(12)
			.isCompleted(true)
			.detailCategory(detailCategories)
			.build();

		given(calculateSingleDetailGraduationUseCase.calculateSingleDetailGraduation(1L,
			CORE_CULTURE)).willReturn(detailGraduationResult);

		//when //then
		mockMvc.perform(get("/api/v1/graduations/detail")
				.param("graduationCategory", "CORE_CULTURE"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalCredit").value(12))
			.andExpect(jsonPath("$.takenCredit").value(12))
			.andExpect(jsonPath("$.completed").value(true))
			.andExpect(jsonPath("$.detailCategory.length()").value(detailCategories.size()));
	}

	@WithMockAuthenticationUser
	@DisplayName("주전공필수 졸업 상세 결과를 조회한다.")
	@Test
	void getPrimaryMandatoryMajorDetailGraduations() throws Exception {
		//given
		List<DetailCategoryResult> detailCategories = List.of(
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build()
		);
		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.graduationCategory(PRIMARY_MANDATORY_MAJOR)
			.totalCredit(18)
			.takenCredit(18)
			.isCompleted(true)
			.detailCategory(detailCategories)
			.build();

		given(calculateSingleDetailGraduationUseCase.calculateSingleDetailGraduation(1L,
			PRIMARY_MANDATORY_MAJOR)).willReturn(detailGraduationResult);

		//when //then
		mockMvc.perform(get("/api/v1/graduations/detail")
				.param("graduationCategory", "PRIMARY_MANDATORY_MAJOR"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalCredit").value(18))
			.andExpect(jsonPath("$.takenCredit").value(18))
			.andExpect(jsonPath("$.completed").value(true))
			.andExpect(jsonPath("$.detailCategory.length()").value(detailCategories.size()));
	}

	@WithMockAuthenticationUser
	@DisplayName("주전공선택 졸업 상세 결과를 조회한다.")
	@Test
	void getPrimaryElectiveMajorDetailGraduations() throws Exception {
		//given
		List<DetailCategoryResult> detailCategories = List.of(
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build()
		);
		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.graduationCategory(PRIMARY_ELECTIVE_MAJOR)
			.totalCredit(58)
			.takenCredit(12)
			.isCompleted(false)
			.detailCategory(detailCategories)
			.build();

		given(calculateSingleDetailGraduationUseCase.calculateSingleDetailGraduation(1L,
			PRIMARY_ELECTIVE_MAJOR)).willReturn(detailGraduationResult);

		//when //then
		mockMvc.perform(get("/api/v1/graduations/detail")
				.param("graduationCategory", "PRIMARY_ELECTIVE_MAJOR"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalCredit").value(58))
			.andExpect(jsonPath("$.takenCredit").value(12))
			.andExpect(jsonPath("$.completed").value(false))
			.andExpect(jsonPath("$.detailCategory.length()").value(detailCategories.size()));
	}

	@WithMockAuthenticationUser
	@DisplayName("주학문기초교양 졸업 상세 결과를 조회한다.")
	@Test
	void getPrimaryBasicAcademicalCultureMajorDetailGraduations() throws Exception {
		//given
		List<DetailCategoryResult> detailCategories = List.of(
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build(),
			DetailCategoryResult.builder()
				.build()
		);
		DetailGraduationResult detailGraduationResult = DetailGraduationResult.builder()
			.graduationCategory(PRIMARY_BASIC_ACADEMICAL_CULTURE)
			.totalCredit(18)
			.takenCredit(18)
			.isCompleted(true)
			.detailCategory(detailCategories)
			.build();

		given(calculateSingleDetailGraduationUseCase.calculateSingleDetailGraduation(1L,
			PRIMARY_BASIC_ACADEMICAL_CULTURE)).willReturn(detailGraduationResult);

		//when //then
		mockMvc.perform(get("/api/v1/graduations/detail")
				.param("graduationCategory", "PRIMARY_BASIC_ACADEMICAL_CULTURE"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalCredit").value(18))
			.andExpect(jsonPath("$.takenCredit").value(18))
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
			.andExpect(jsonPath("$.errorCode").value(INVALIDATED_GRADUATION_CATEGORY.toString()));
	}

}
