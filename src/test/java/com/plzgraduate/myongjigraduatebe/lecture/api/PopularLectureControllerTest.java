package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.PopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PopularLectureController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class PopularLectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PopularLecturesUseCase popularLecturesUseCase;

    

    @Test
    @DisplayName("/popular/by-category 초기: category=ALL → 동일 형태(카테고리 페이지)로 반환")
    void getPopularLecturesByCategory_init_all() throws Exception {
        var lectures = List.of(
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA001").name("과목1").credit(3).averageRating(4.5).totalCount(100L)
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA002").name("과목2").credit(3).averageRating(4.4).totalCount(90L)
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .build()
        );
        var categoryResp = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse.of(
                PopularLectureCategory.CORE_CULTURE, lectures, 10
        );
        when(popularLecturesUseCase.getPopularLectures("응용소프트웨어전공", 24, PopularLectureCategory.ALL, 10, null))
                .thenReturn(categoryResp);

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "응용소프트웨어전공")
                        .param("entryYear", "24")
                        .param("category", "ALL")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is("CORE_CULTURE")))
                .andExpect(jsonPath("$.lectures", hasSize(2)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)));
    }

    @Test
    @DisplayName("/popular/by-category 페이지: category=MANDATORY_MAJOR → categoryName + lectures + pageInfo")
    void getPopularLecturesByCategory_specific() throws Exception {
        var lectures = List.of(
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA010").name("데이터베이스").credit(3).averageRating(4.2).totalCount(999L)
                        .categoryName(PopularLectureCategory.MANDATORY_MAJOR)
                        .build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA011").name("운영체제").credit(3).averageRating(4.1).totalCount(888L)
                        .categoryName(PopularLectureCategory.MANDATORY_MAJOR)
                        .build()
        );
        var categoryResp = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse.of(
                PopularLectureCategory.MANDATORY_MAJOR, lectures, 10
        );
        when(popularLecturesUseCase.getPopularLectures("응용소프트웨어전공", 24, PopularLectureCategory.MANDATORY_MAJOR, 10, null))
                .thenReturn(categoryResp);

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "응용소프트웨어전공")
                        .param("entryYear", "24")
                        .param("category", "MANDATORY_MAJOR")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is("MANDATORY_MAJOR")))
                .andExpect(jsonPath("$.lectures", hasSize(2)))
                .andExpect(jsonPath("$.lectures[0].id", is("KMA010")))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(10)));
    }

    @Test
    @DisplayName("ALL 시작 → 2번 스크롤 후 카테고리 종료 → 다음 카테고리로 전환 흐름")
    void flow_all_then_next_category() throws Exception {
        // 1) 첫 호출: ALL, 서버 기본 카테고리 CORE_CULTURE, limit=2, hasMore=true (3개 전달 → 2개 노출)
        var firstItems = List.of(
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA001").name("코어1").credit(3).averageRating(4.5).totalCount(100L)
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA002").name("코어2").credit(3).averageRating(4.4).totalCount(99L)
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA003").name("코어3").credit(3).averageRating(4.3).totalCount(98L)
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .build()
        );
        var resp1 = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse.of(
                PopularLectureCategory.CORE_CULTURE, firstItems, 2
        );
        when(popularLecturesUseCase.getPopularLectures("경영학전공", 19, PopularLectureCategory.ALL, 2, null))
                .thenReturn(resp1);

        // 2) 두 번째 호출: 같은 카테고리 CORE_CULTURE, cursor=KMA002, 남은 1개만 → hasMore=false
        var secondItems = List.of(
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA003").name("코어3").credit(3).averageRating(4.3).totalCount(98L)
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .build()
        );
        var resp2 = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse.of(
                PopularLectureCategory.CORE_CULTURE, secondItems, 2
        );
        when(popularLecturesUseCase.getPopularLectures("경영학전공", 19, PopularLectureCategory.CORE_CULTURE, 2, "KMA002"))
                .thenReturn(resp2);

        // 3) 다음 카테고리로 전환: MANDATORY_MAJOR, 첫 페이지 2개 노출, hasMore=true
        var thirdItems = List.of(
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("HMD001").name("MANDATORY_MAJOR_1").credit(3).averageRating(4.7).totalCount(150L)
                        .categoryName(PopularLectureCategory.MANDATORY_MAJOR)
                        .build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("HMD002").name("MANDATORY_MAJOR_2").credit(3).averageRating(4.6).totalCount(149L)
                        .categoryName(PopularLectureCategory.MANDATORY_MAJOR)
                        .build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("HMD003").name("MANDATORY_MAJOR_3").credit(3).averageRating(4.5).totalCount(148L)
                        .categoryName(PopularLectureCategory.MANDATORY_MAJOR)
                        .build()
        );
        var resp3 = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse.of(
                PopularLectureCategory.MANDATORY_MAJOR, thirdItems, 2
        );
        when(popularLecturesUseCase.getPopularLectures("경영학전공", 19, PopularLectureCategory.MANDATORY_MAJOR, 2, null))
                .thenReturn(resp3);

        // 호출 1: ALL → CORE_CULTURE 2개, hasMore=true
        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "경영학전공")
                        .param("entryYear", "19")
                        .param("category", "ALL")
                        .param("limit", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is("CORE_CULTURE")))
                .andExpect(jsonPath("$.lectures", hasSize(2)))
                .andExpect(jsonPath("$.lectures[0].id", is("KMA001")))
                .andExpect(jsonPath("$.lectures[1].id", is("KMA002")))
                .andExpect(jsonPath("$.pageInfo.hasMore", is(true)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(2)));

        // 호출 2: CORE_CULTURE 이어서 → 남은 1개, hasMore=false
        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "경영학전공")
                        .param("entryYear", "19")
                        .param("category", "CORE_CULTURE")
                        .param("limit", "2")
                        .param("cursor", "KMA002")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is("CORE_CULTURE")))
                .andExpect(jsonPath("$.lectures", hasSize(1)))
                .andExpect(jsonPath("$.lectures[0].id", is("KMA003")))
                .andExpect(jsonPath("$.pageInfo.hasMore", is(false)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(2)));

        // 호출 3: 다음 카테고리 MANDATORY_MAJOR로 전환 → 2개 노출, hasMore=true
        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "경영학전공")
                        .param("entryYear", "19")
                        .param("category", "MANDATORY_MAJOR")
                        .param("limit", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName", is("MANDATORY_MAJOR")))
                .andExpect(jsonPath("$.lectures", hasSize(2)))
                .andExpect(jsonPath("$.lectures[0].id", is("HMD001")))
                .andExpect(jsonPath("$.lectures[1].id", is("HMD002")))
                .andExpect(jsonPath("$.pageInfo.hasMore", is(true)))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(2)));
    }
}
