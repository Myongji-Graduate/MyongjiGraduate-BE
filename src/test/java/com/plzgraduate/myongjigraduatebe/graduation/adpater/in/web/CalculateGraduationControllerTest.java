package com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.BasicInfoResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.ChapelResultResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.DetailGraduationResultResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.RestResultResponse;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;

class CalculateGraduationControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("유저의 졸업 결과를 계산한다.")
	@Test
	void calculate() throws Exception {
	    ///given
		Long userId = 1L;
		GraduationResponse response = GraduationResponse.builder()
			.basicInfo(BasicInfoResponse.builder().build())
			.chapelResult(ChapelResultResponse.builder().build())
			.commonCulture(DetailGraduationResultResponse.builder().build())
			.coreCulture(DetailGraduationResultResponse.builder().build())
			.basicAcademicalCulture(DetailGraduationResultResponse.builder().build())
			.major(DetailGraduationResultResponse.builder().build())
			.normalCulture(RestResultResponse.builder().build())
			.freeElective(RestResultResponse.builder().build())
			.graduated(true).build();
		given(calculateGraduationUseCase.calculateGraduation(userId)).willReturn(response);

		//when //then
		mockMvc.perform(
			get("/api/v1/graduation/result")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.basicInfo").exists())
			.andExpect(jsonPath("$.chapelResult").exists())
			.andExpect(jsonPath("$.commonCulture").exists())
			.andExpect(jsonPath("$.coreCulture").exists())
			.andExpect(jsonPath("$.basicAcademicalCulture").exists())
			.andExpect(jsonPath("$.major").exists())
			.andExpect(jsonPath("$.normalCulture").exists())
			.andExpect(jsonPath("$.freeElective").exists())
			.andExpect(jsonPath("$.graduated").isBoolean());
	}
}
