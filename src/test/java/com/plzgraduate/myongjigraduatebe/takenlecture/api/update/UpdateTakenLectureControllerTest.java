package com.plzgraduate.myongjigraduatebe.takenlecture.api.update;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.GenerateCustomizedTakenLectureRequest;

class UpdateTakenLectureControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("수강과목을 생성한다.")
	@Test
	void generateTakenLecture() throws Exception {
		//given
		GenerateCustomizedTakenLectureRequest request = GenerateCustomizedTakenLectureRequest.builder()
			.lectureId(1L)
			.build();

		//when //then
		mockMvc.perform(
				post("/api/v1/taken-lectures")
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf())
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@WithMockAuthenticationUser
	@DisplayName("수강과목을 삭제한다.")
	@Test
	void deleteTakenLecture() throws Exception {
		//given
		Long takenLectureId = 1L;

		//when //then
		mockMvc.perform(
				delete("/api/v1/taken-lectures/{taken-lecture-id}", takenLectureId))
			.andDo(print())
			.andExpect(status().isOk());
	}
}
