package com.plzgraduate.myongjigraduatebe.lecturedetail.api;

import com.plzgraduate.myongjigraduatebe.lecturedetail.application.service.FindLectureInfoService;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FindLectureInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
class FindLectureInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FindLectureInfoService findLectureInfoService;

    @Test
    @DisplayName("강의 정보 조회 API 성공")
    void shouldReturnLectureInfoWhenValidParams() throws Exception {
        // given
        String subject = "성서와인간이해";
        String professor = "조내연";

        String assignmentText = "없음";
        String attendance = "두 번";
        String exam = "보통";
        BigDecimal rating = BigDecimal.valueOf(4.07);
        String grading = "없음";
        String teamwork = "없음";

        LectureInfo domain = LectureInfo.builder()
                .subject(subject)
                .professor(professor)
                .assignment(assignmentText)
                .attendance(attendance)
                .exam(exam)
                .grading(grading)
                .teamwork(teamwork)
                .rating(rating)
                .build();

        given(findLectureInfoService.findLectureInfoBySubjectAndProfessor(subject, professor)).willReturn(domain);

        // when & then
        mockMvc.perform(get("/api/v1/lectures-info")
                        .param("subject", subject)
                        .param("professor", professor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject").value(subject))
                .andExpect(jsonPath("$.professor").value(professor))
                .andExpect(jsonPath("$.assignment").value(assignmentText))
                .andExpect(jsonPath("$.attendance").value("두 번"))
                .andExpect(jsonPath("$.rating").value(4.07))
                .andExpect(jsonPath("$.exam").value("보통"))
                .andExpect(jsonPath("$.grading").value(grading))
                .andExpect(jsonPath("$.teamwork").value(teamwork));
    }

    @Test
    @DisplayName("필수 파라미터 누락 시 400")
    void shouldReturnBadRequestWhenMissingParams() throws Exception {
        mockMvc.perform(get("/api/v1/lectures-info")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}