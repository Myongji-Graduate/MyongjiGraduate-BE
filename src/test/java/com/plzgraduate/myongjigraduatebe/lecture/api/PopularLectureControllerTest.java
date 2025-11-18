package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.PopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PopularLectureController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@org.springframework.context.annotation.Import(com.plzgraduate.myongjigraduatebe.core.exception.GlobalExceptionHandler.class)
class PopularLectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PopularLecturesUseCase useCase;

    @Test
    @DisplayName("/lectures/popular - 초기 빈 결과는 404와 NO_POPULAR_LECTURES 반환")
    void popular_initial_empty_returns_404() throws Exception {
        given(useCase.getPopularLectures(anyInt(), isNull()))
                .willThrow(new java.util.NoSuchElementException(ErrorCode.NO_POPULAR_LECTURES.toString()));

        mockMvc.perform(get("/api/v1/lectures/popular")
                        .param("limit", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.NO_POPULAR_LECTURES.toString()));
    }

    @Test
    @DisplayName("/lectures/popular - 페이지 끝은 200과 빈 리스트(hasMore=false)")
    void popular_page_end_returns_200_empty() throws Exception {
        List<PopularLectureResponse> items = Collections.emptyList();
        PopularLecturesPageResponse page = PopularLecturesPageResponse.of(items, 10);
        given(useCase.getPopularLectures(eq(10), anyString())).willReturn(page);

        mockMvc.perform(get("/api/v1/lectures/popular")
                        .param("limit", "10")
                        .param("cursor", "LAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectures", hasSize(0)))
                .andExpect(jsonPath("$.pageInfo.hasMore").value(false))
                .andExpect(jsonPath("$.pageInfo.nextCursor", nullValue()));
    }

    @Test
    @DisplayName("/lectures/popular/by-category - category=ALL 섹션 총합 0이면 404")
    void by_category_all_sections_empty_returns_404() throws Exception {
        given(useCase.getInitPopularLectures(anyString(), anyInt(), anyInt(), isNull()))
                .willThrow(new java.util.NoSuchElementException(ErrorCode.NO_POPULAR_LECTURES.toString()));

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "컴퓨터공학")
                        .param("entryYear", "2020")
                        .param("category", "ALL")
                        .param("limit", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.NO_POPULAR_LECTURES.toString()));
    }

    @Test
    @DisplayName("/lectures/popular/by-category - 특정 카테고리 초기 결과 0이면 404")
    void by_category_specific_initial_empty_returns_404() throws Exception {
        given(useCase.getPopularLecturesByCategory(anyString(), anyInt(), org.mockito.ArgumentMatchers.any(PopularLectureCategory.class), anyInt(), isNull()))
                .willThrow(new java.util.NoSuchElementException(ErrorCode.NO_POPULAR_LECTURES.toString()));

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "컴퓨터공학")
                        .param("entryYear", "2020")
                        .param("category", "MANDATORY_MAJOR")
                        .param("limit", "10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.NO_POPULAR_LECTURES.toString()));
    }

    @Test
    @DisplayName("/lectures/popular/by-category - 특정 카테고리 페이지 끝은 200 + 빈 리스트")
    void by_category_specific_page_end_returns_200_empty() throws Exception {
        PopularLecturesByCategoryResponse resp = PopularLecturesByCategoryResponse.of(
                PopularLectureCategory.MANDATORY_MAJOR,
                Collections.emptyList(),
                10
        );
        given(useCase.getPopularLecturesByCategory(anyString(), anyInt(), org.mockito.ArgumentMatchers.any(PopularLectureCategory.class), anyInt(), anyString()))
                .willReturn(resp);

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "컴퓨터공학")
                        .param("entryYear", "2020")
                        .param("category", "MANDATORY_MAJOR")
                        .param("limit", "10")
                        .param("cursor", "LAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("MANDATORY_MAJOR"))
                .andExpect(jsonPath("$.lectures", hasSize(0)))
                .andExpect(jsonPath("$.pageInfo.hasMore").value(false))
                .andExpect(jsonPath("$.pageInfo.nextCursor", nullValue()));
    }

    @Test
    @DisplayName("/lectures/popular/by-category - 필수 파라미터 누락은 400")
    void by_category_missing_required_param_returns_400() throws Exception {
        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        // missing major
                        .param("entryYear", "2020")
                        .param("category", "MAJOR_REQUIRED")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", containsString("MISSING_REQUEST_PARAMETER")));
    }

    @Test
    @DisplayName("/lectures/popular/by-category - enum 매핑 실패는 400 + INVALIDATED_GRADUATION_CATEGORY")
    void by_category_enum_mismatch_returns_400() throws Exception {
        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "컴퓨터공학")
                        .param("entryYear", "2020")
                        .param("category", "NOT_A_VALID_CATEGORY")
                        .param("limit", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INVALIDATED_GRADUATION_CATEGORY.toString()));
    }

    @Test
    @DisplayName("/lectures/popular - limit < 1 은 400 + 검증 메시지")
    void popular_limit_min_validation_returns_400() throws Exception {
        mockMvc.perform(get("/api/v1/lectures/popular")
                        .param("limit", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("limit은 1 이상이어야 합니다."));
    }

    @Test
    @DisplayName("/lectures/popular - 내부 예외는 500 + INTERNAL_SEVER_ERROR")
    void popular_unexpected_exception_returns_500() throws Exception {
        given(useCase.getPopularLectures(anyInt(), any()))
                .willAnswer(invocation -> { throw new RuntimeException("boom"); });

        mockMvc.perform(get("/api/v1/lectures/popular")
                        .param("limit", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.INTERNAL_SEVER_ERROR.toString()));
    }
}
