package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.in.web.find;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.find.TakenLectureResponse;

@WebMvcTest(controllers = FindTakenLectureController.class)
class FindTakenLectureControllerTest extends WebAdaptorTestSupport {

	@MockBean
	FindTakenLectureUseCase findTakenLectureUseCase;

	@WithMockAuthenticationUser
	@DisplayName("사용자의 수강과목을 조회한다.")
	@Test
	void getTakenLectures() throws Exception {
		//given
		List<TakenLectureResponse> takenLectures = new ArrayList<>();
		FindTakenLectureResponse response = FindTakenLectureResponse.builder()
			.totalCredit(10)
			.takenLectures(takenLectures)
			.build();
		given(findTakenLectureUseCase.getTakenLectures(anyLong())).willReturn(response);

		//when //when
		mockMvc.perform(
			get("/api/v1/taken-lectures")
		)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.totalCredit").value(10))
			.andExpect(jsonPath("$.takenLectures").isArray());
	}
}
