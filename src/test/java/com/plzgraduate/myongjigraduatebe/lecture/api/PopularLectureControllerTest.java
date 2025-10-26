package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.PopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
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

    private List<PopularLectureDto> sampleLectures() {
        return List.of(
                PopularLectureDto.of("KMA001", "과목1", 3, 100L, PopularLectureCategory.NORMAL_CULTURE),
                PopularLectureDto.of("KMA002", "과목2", 3, 90L, PopularLectureCategory.NORMAL_CULTURE),
                PopularLectureDto.of("KMA003", "과목3", 3, 80L, PopularLectureCategory.NORMAL_CULTURE),
                PopularLectureDto.of("KMA004", "과목4", 3, 70L, PopularLectureCategory.NORMAL_CULTURE),
                PopularLectureDto.of("KMA005", "과목5", 3, 60L, PopularLectureCategory.NORMAL_CULTURE)
        );
    }

    @Test
    @DisplayName("/popular 첫 페이지: limit=2이면 2개 반환, hasMore=true, nextCursor=두번째ID")
    void getPopularLectures_firstPage_withHasMore() throws Exception {
        when(popularLecturesUseCase.getPopularLecturesByTotalCount())
                .thenReturn(sampleLectures());

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
        when(popularLecturesUseCase.getPopularLecturesByTotalCount())
                .thenReturn(sampleLectures());

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
        when(popularLecturesUseCase.getPopularLecturesByTotalCount())
                .thenReturn(sampleLectures());

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
}
