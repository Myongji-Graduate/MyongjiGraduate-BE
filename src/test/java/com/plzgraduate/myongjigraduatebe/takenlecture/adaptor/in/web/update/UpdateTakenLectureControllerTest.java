package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.update;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;

class UpdateTakenLectureControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("수강과목을 수정한다.")
	@Test
	void updateTakenLectures() throws Exception{
		//given
		UpdateTakenLectureRequest request = UpdateTakenLectureRequest.builder()
			.addedTakenLectures(List.of(1L, 2L))
			.deletedTakenLectures(List.of(1L, 2L))
			.build();

		//when //then
		mockMvc.perform(
			post("/api/v1/taken-lectures/update")
				.content(objectMapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
		)
			.andDo(print())
			.andExpect(status().isOk());
	}

}
