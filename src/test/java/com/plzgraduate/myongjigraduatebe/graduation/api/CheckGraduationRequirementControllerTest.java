package com.plzgraduate.myongjigraduatebe.graduation.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.request.CheckGraduationRequirementRequest;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingAnonymousUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(CheckGraduationRequirementController.class)
@AutoConfigureMockMvc(addFilters = false)
class CheckGraduationRequirementControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ParsingAnonymousUseCase parsingAnonymousUseCase;

	@MockBean
	private CheckGraduationRequirementUseCase checkGraduationRequirementUseCase;

	@DisplayName("비회원 졸업진단 응답의 user 정보는 계산된 졸업 결과를 반영한다.")
	@Test
	void checkGraduationRequirementUsesGraduationResultForUserInfo() throws Exception {
		CheckGraduationRequirementRequest request = CheckGraduationRequirementRequest.builder()
			.engLv("ENG34")
			.korLv("FREE")
			.parsingText("mock parsing text")
			.build();

		User anonymous = User.builder()
			.authId("anonymous")
			.name("방현우")
			.studentNumber("60190872")
			.primaryMajor("국제통상학과")
			.dualMajor("경영정보학과")
			.totalCredit(0)
			.takenCredit(0)
			.graduated(false)
			.build();

		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(
			anonymous,
			TakenLectureInventory.from(Set.of())
		);

		GraduationResult graduationResult = GraduationResult.builder()
			.totalCredit(128)
			.takenCredit(133)
			.graduated(true)
			.build();

		given(parsingAnonymousUseCase.parseAnonymous(EnglishLevel.ENG34, KoreanLevel.FREE, "mock parsing text"))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(any(User.class), any(TakenLectureInventory.class)))
			.willReturn(graduationResult);

		mockMvc.perform(
				post("/api/v1/graduations/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.user.studentNumber").value("60190872"))
			.andExpect(jsonPath("$.user.totalCredit").value(128))
			.andExpect(jsonPath("$.user.takenCredit").value(133))
			.andExpect(jsonPath("$.user.graduated").value(true));
	}
}
