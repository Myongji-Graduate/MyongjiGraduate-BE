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
import java.util.Collections;
import java.util.List;

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
    @DisplayName("강좌명으로 교수별 강좌 정보 리스트 조회 성공")
    void shouldReturnLectureInfoListWhenValidSubject() throws Exception {
        // given
        String subject = "성서와인간이해";

        LectureInfo profA = LectureInfo.builder()
                .subject(subject)
                .professor("강안일")
                .assignment("없음")
                .attendance("출석 체크")
                .exam("한 번")
                .grading("담당교수 재량")
                .teamwork("없음")
                .rating(new BigDecimal("5.0"))
                .lectureReviews(Collections.emptyList())
                .build();

        LectureInfo profB = LectureInfo.builder()
                .subject(subject)
                .professor("조내연")
                .assignment("없음")
                .attendance("두 번")
                .exam("보통")
                .grading("보통")
                .teamwork("없음")
                .rating(new BigDecimal("4.07"))
                .lectureReviews(Collections.emptyList())
                .build();

        given(findLectureInfoService.findLectureInfoBySubject(subject))
                .willReturn(List.of(profA, profB));

        // when & then
        mockMvc.perform(get("/api/v1/lectures-info")
                        .param("subject", subject)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // 리스트이므로 [0], [1] 인덱싱
                .andExpect(jsonPath("$[0].professor").value("강안일"))
                .andExpect(jsonPath("$[1].professor").value("조내연"))
                .andExpect(jsonPath("$[0].subject").value(subject));
    }

    @Test
    @DisplayName("subject 파라미터 없으면 400 반환")
    void shouldReturnBadRequestWhenMissingSubject() throws Exception {
        mockMvc.perform(get("/api/v1/lectures-info")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}