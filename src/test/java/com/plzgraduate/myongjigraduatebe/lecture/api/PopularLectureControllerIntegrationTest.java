package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PopularLectureControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Service가 참조하는 포트를 목 처리해 DB 의존 제거
    @MockBean
    private PopularLecturePort popularLecturePort;

    @Test
    @DisplayName("[통합] 전공/학번 미매핑이면 어떤 카테고리든 400 + UNSUPPORTED_STUDENT_CATEGORY 응답")
    void invalid_mapping_returns_bad_request_for_all_categories() throws Exception {
        // ALL 요청: 서비스 레이어 사전 검증에서 차단 → 포트 미호출, 400 반환
        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "인공지능전공")
                        .param("entryYear", "24")
                        .param("category", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.UNSUPPORTED_STUDENT_CATEGORY.toString())));

        verify(popularLecturePort, never())
                .getLecturesByCategory(anyString(), anyInt(), any(), anyInt(), any());

        // COMMON_CULTURE 요청도 동일하게 400
        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "인공지능전공")
                        .param("entryYear", "24")
                        .param("category", "COMMON_CULTURE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is(ErrorCode.UNSUPPORTED_STUDENT_CATEGORY.toString())));
    }

    @Test
    @DisplayName("[통합] 유효 전공/학번이면 정상 200: COMMON_CULTURE 예시")
    void valid_mapping_returns_ok() throws Exception {
        var dto1 = PopularLectureDto.ofWithAverage("KMA001", "공통1", 3, 100L, PopularLectureCategory.COMMON_CULTURE, 4.6);
        var dto2 = PopularLectureDto.ofWithAverage("KMA002", "공통2", 3, 90L, PopularLectureCategory.COMMON_CULTURE, 4.5);
        when(popularLecturePort.getLecturesByCategory("응용소프트웨어전공", 24, PopularLectureCategory.COMMON_CULTURE, 10, null))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "응용소프트웨어전공")
                        .param("entryYear", "24")
                        .param("category", "COMMON_CULTURE")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is("COMMON_CULTURE")))
                .andExpect(jsonPath("$.lectures[0].id", is("KMA001")))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)));
    }
}

