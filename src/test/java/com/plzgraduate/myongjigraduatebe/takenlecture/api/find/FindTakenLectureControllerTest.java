package com.plzgraduate.myongjigraduatebe.takenlecture.api.find;

import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;

class FindTakenLectureControllerTest extends WebAdaptorTestSupport {

	@WithMockAuthenticationUser
	@DisplayName("사용자의 수강과목을 조회한다.")
	@Test
	void getTakenLectures() throws Exception {
		//given
		HashSet<TakenLecture> takenLectures = new HashSet<>(Set.of(
			TakenLecture.builder()
				.lecture(Lecture.builder()
					.lectureCode("KMA")
					.credit(4).build())
				.year(2020)
				.semester(Semester.FIRST).build(),
			TakenLecture.builder()
				.lecture(Lecture.builder()
					.lectureCode("KMB")
					.credit(3).build())
				.year(2020)
				.semester(Semester.SECOND).build(),
			TakenLecture.builder()
				.lecture(Lecture.builder()
					.lectureCode("KMC")
					.credit(3).build())
				.year(2021)
				.semester(Semester.FIRST).build()
		));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		given(findTakenLectureUseCase.findTakenLectures(anyLong())).willReturn(takenLectureInventory);

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
