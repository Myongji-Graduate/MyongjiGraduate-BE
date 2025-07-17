package com.plzgraduate.myongjigraduatebe.takenlecture.api.update;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.parsing.api.TakenLectureCacheEvict;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request.GenerateCustomizedTakenLectureRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

class UpdateTakenLectureControllerTest extends WebAdaptorTestSupport {

	@MockBean
	private TakenLectureCacheEvict takenLectureCacheEvict;

	@WithMockAuthenticationUser
	@DisplayName("수강과목을 생성한다.")
	@Test
	void generateTakenLecture() throws Exception {
		//given
		GenerateCustomizedTakenLectureRequest request = GenerateCustomizedTakenLectureRequest.builder()
			.lectureId("KMA02137")
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
