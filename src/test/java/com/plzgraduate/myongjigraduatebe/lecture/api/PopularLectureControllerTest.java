package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
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


    private List<com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse> sampleLectureResponses() {
        return List.of(
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder().id("KMA001").name("과목1").credit(3).averageRating(4.0).totalCount(100L).categoryName(PopularLectureCategory.NORMAL_CULTURE).build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder().id("KMA002").name("과목2").credit(3).averageRating(4.0).totalCount(90L).categoryName(PopularLectureCategory.NORMAL_CULTURE).build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder().id("KMA003").name("과목3").credit(3).averageRating(4.0).totalCount(80L).categoryName(PopularLectureCategory.NORMAL_CULTURE).build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder().id("KMA004").name("과목4").credit(3).averageRating(4.0).totalCount(70L).categoryName(PopularLectureCategory.NORMAL_CULTURE).build(),
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder().id("KMA005").name("과목5").credit(3).averageRating(4.0).totalCount(60L).categoryName(PopularLectureCategory.NORMAL_CULTURE).build()
        );
    }

    @Test
    @DisplayName("/popular 첫 페이지: limit=2이면 2개 반환, hasMore=true, nextCursor=두번째ID")
    void getPopularLectures_firstPage_withHasMore() throws Exception {
        List<com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse> all = sampleLectureResponses();
        // service will have done slicing; controller returns what service provides
        var firstPageSource = List.of(all.get(0), all.get(1), all.get(2));
        var pageResp = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse.of(firstPageSource, 2);
        when(popularLecturesUseCase.getPopularLectures(2, null))
                .thenReturn(pageResp);

        mockMvc.perform(get("/api/v1/lectures/popular")
                        .param("limit", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectures", hasSize(2)))
                .andExpect(jsonPath("$.lectures[0].id", is("KMA001")))
                .andExpect(jsonPath("$.lectures[1].id", is("KMA002")))
                .andExpect(jsonPath("$.pageInfo.hasMore", is(true)))
                .andExpect(jsonPath("$.pageInfo.nextCursor", is("KMA002")))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(2)));
    }

    @Test
    @DisplayName("/popular 두번째 페이지: cursor=KMA002, limit=2 → KMA003,KMA004, hasMore=true, nextCursor=KMA004")
    void getPopularLectures_secondPage_withHasMore() throws Exception {
        List<com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse> all = sampleLectureResponses();
        var secondPageSource = List.of(all.get(2), all.get(3), all.get(4));
        var pageResp = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse.of(secondPageSource, 2);
        when(popularLecturesUseCase.getPopularLectures(2, "KMA002"))
                .thenReturn(pageResp);

        mockMvc.perform(get("/api/v1/lectures/popular")
                        .param("limit", "2")
                        .param("cursor", "KMA002")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectures", hasSize(2)))
                .andExpect(jsonPath("$.lectures[0].id", is("KMA003")))
                .andExpect(jsonPath("$.lectures[1].id", is("KMA004")))
                .andExpect(jsonPath("$.pageInfo.hasMore", is(true)))
                .andExpect(jsonPath("$.pageInfo.nextCursor", is("KMA004")))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(2)));
    }

    @Test
    @DisplayName("/popular 마지막 페이지: cursor=KMA004, limit=2 → KMA005만 반환, hasMore=false, nextCursor=null")
    void getPopularLectures_lastPage_noMore() throws Exception {
        List<com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse> all = sampleLectureResponses();
        var lastPageSource = List.of(all.get(4));
        var pageResp = com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse.of(lastPageSource, 2);
        when(popularLecturesUseCase.getPopularLectures(2, "KMA004"))
                .thenReturn(pageResp);

        mockMvc.perform(get("/api/v1/lectures/popular")
                        .param("limit", "2")
                        .param("cursor", "KMA004")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectures", hasSize(1)))
                .andExpect(jsonPath("$.lectures[0].id", is("KMA005")))
                .andExpect(jsonPath("$.pageInfo.hasMore", is(false)))
                .andExpect(jsonPath("$.pageInfo.nextCursor", nullValue()))
                .andExpect(jsonPath("$.pageInfo.pageSize", is(2)));
    }

    @Test
    @DisplayName("/popular/by-category 초기: category=ALL → sections + primeSection 반환")
    void getPopularLecturesByCategory_init_all() throws Exception {
        var sections = List.of(
                PopularLecturesInitResponse.SectionMeta.builder()
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .total(3)
                        .build()
        );
        var lectures = List.of(
                com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse.builder()
                        .id("KMA001").name("과목1").credit(3).averageRating(4.5).totalCount(100L)
                        .categoryName(PopularLectureCategory.CORE_CULTURE)
                        .build()
        );
        PopularLecturesInitResponse init = PopularLecturesInitResponse.of(
                sections, PopularLectureCategory.ALL, lectures, 10
        );
        when(popularLecturesUseCase.getInitPopularLectures("응용소프트웨어전공", 24, 10, null))
                .thenReturn(init);

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                        .param("major", "응용소프트웨어전공")
                        .param("entryYear", "24")
                        .param("category", "ALL")
                        .param("limit", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sections", hasSize(1)))
                .andExpect(jsonPath("$.primeSection.categoryName", is("ALL")))
                .andExpect(jsonPath("$.primeSection.lectures", hasSize(1)))
                .andExpect(jsonPath("$.primeSection.lectures[0].id", is("KMA001")));
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
        when(popularLecturesUseCase.getPopularLecturesByCategory("응용소프트웨어전공", 24, PopularLectureCategory.MANDATORY_MAJOR, 10, null))
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
}
