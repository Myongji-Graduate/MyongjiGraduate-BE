package com.plzgraduate.myongjigraduatebe.completedcredit.api;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FindCompletedCreditsControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("유저의 이수 학점을 조회한다.")
	@Test
	void findCompletedCredits() throws Exception {
		//given
		List<CompletedCredit> completedCredits = List.of(
			CompletedCredit.builder()
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(10)
				.takenCredit(10)
				.build(),
			CompletedCredit.builder()
				.graduationCategory(GraduationCategory.CORE_CULTURE)
				.totalCredit(10)
				.takenCredit(5)
				.build());
		given(findCompletedCreditUseCase.findCompletedCredits(1L)).willReturn(completedCredits);

		//when //then
		mockMvc.perform(get("/api/v1/graduations/credits"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(completedCredits.size()))
			.andExpect(jsonPath("$[0].category").value(GraduationCategory.COMMON_CULTURE.name()))
			.andExpect(jsonPath("$[0].totalCredit").value(10))
			.andExpect(jsonPath("$[0].takenCredit").value(10))
			.andExpect(jsonPath("$[0].completed").value(true))
			.andExpect(jsonPath("$[1].category").value(GraduationCategory.CORE_CULTURE.name()))
			.andExpect(jsonPath("$[1].totalCredit").value(10))
			.andExpect(jsonPath("$[1].takenCredit").value(5))
			.andExpect(jsonPath("$[1].completed").value(false));

	}


	@WithMockAuthenticationUser
	@DisplayName("유저의 이수 학점을 조회 시 totalCredit이 0인 CompletedCredit은 조회하지 않는다.")
	@Test
	void findCompletedCreditsWithZeroTotalCredit() throws Exception {
		//given
		List<CompletedCredit> completedCredits = List.of(
			CompletedCredit.builder()
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(0)
				.takenCredit(10)
				.build(),
			CompletedCredit.builder()
				.graduationCategory(GraduationCategory.CORE_CULTURE)
				.totalCredit(0)
				.takenCredit(5)
				.build());
		given(findCompletedCreditUseCase.findCompletedCredits(1L)).willReturn(completedCredits);

		//when //then
		mockMvc.perform(get("/api/v1/graduations/credits"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}
}
