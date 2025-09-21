package com.plzgraduate.myongjigraduatebe.lecture.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.GetPopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.parsing.api.TakenLectureCacheEvict;
import com.plzgraduate.myongjigraduatebe.support.WebAdaptorTestSupport;
import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUser;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class GetPopularLectureControllerTest extends WebAdaptorTestSupport {

    // 일부 테스트 컨텍스트에서 ParsingTextController 의존성으로 필요할 수 있어 안전망으로 Mock 추가
    @MockBean
    private TakenLectureCacheEvict takenLectureCacheEvict;

    @WithMockAuthenticationUser
    @DisplayName("인기 과목 목록을 기본 limit으로 페이지 응답한다.")
    @Test
    void getPopularLectures_defaultLimit() throws Exception {
        List<FindPopularLectureDto> dtos = List.of(
            FindPopularLectureDto.of("LEC-1", "알고리즘", 3, 120L, PopularLectureCategory.MANDATORY_MAJOR),
            FindPopularLectureDto.of("LEC-2", "자료구조", 3, 110L, PopularLectureCategory.ELECTIVE_MAJOR)
        );
        given(getPopularLecturesUseCase.getPopularLecturesByTotalCount()).willReturn(dtos);

        mockMvc.perform(get("/api/v1/lectures/popular"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectures", hasSize(2)))
            .andExpect(jsonPath("$.lectures[0].id").value("LEC-1"))
            .andExpect(jsonPath("$.lectures[0].name").value("알고리즘"))
            .andExpect(jsonPath("$.lectures[0].categoryName").value("전공필수"))
            .andExpect(jsonPath("$.pageInfo.pageSize").value(10))
            .andExpect(jsonPath("$.pageInfo.hasMore").value(false));

        then(getPopularLecturesUseCase).should(times(1)).getPopularLecturesByTotalCount();
    }

    @WithMockAuthenticationUser
    @DisplayName("limit이 작은 경우 페이징 정보를 포함해 잘린 목록을 반환한다.")
    @Test
    void getPopularLectures_withLimitPagination() throws Exception {
        List<FindPopularLectureDto> dtos = List.of(
            FindPopularLectureDto.of("LEC-1", "알고리즘", 3, 120L, PopularLectureCategory.MANDATORY_MAJOR),
            FindPopularLectureDto.of("LEC-2", "자료구조", 3, 110L, PopularLectureCategory.ELECTIVE_MAJOR)
        );
        given(getPopularLecturesUseCase.getPopularLecturesByTotalCount()).willReturn(dtos);

        mockMvc.perform(get("/api/v1/lectures/popular").param("limit", "1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.lectures", hasSize(1)))
            .andExpect(jsonPath("$.lectures[0].id").value("LEC-1"))
            .andExpect(jsonPath("$.pageInfo.pageSize").value(1))
            .andExpect(jsonPath("$.pageInfo.hasMore").value(true))
            .andExpect(jsonPath("$.pageInfo.nextCursor").value("LEC-1"));

        then(getPopularLecturesUseCase).should(times(1)).getPopularLecturesByTotalCount();
    }

    @WithMockAuthenticationUser
    @DisplayName("카테고리 미지정 시 섹션 메타와 프라임 섹션을 반환한다.")
    @Test
    void getPopularLecturesByCategory_init() throws Exception {
        List<PopularLecturesInitResponse.SectionMeta> sections = List.of(
            PopularLecturesInitResponse.SectionMeta.builder()
                .categoryName(PopularLectureCategory.NORMAL_CULTURE)
                .total(2)
                .build()
        );
        List<GetPopularLectureResponse> items = List.of(
            GetPopularLectureResponse.from(
                FindPopularLectureDto.of("LEC-10", "철학입문", 2, 50L, PopularLectureCategory.NORMAL_CULTURE), 0.0),
            GetPopularLectureResponse.from(
                FindPopularLectureDto.of("LEC-11", "심리학개론", 3, 45L, PopularLectureCategory.NORMAL_CULTURE), 0.0)
        );
        PopularLecturesInitResponse response = PopularLecturesInitResponse.of(
            sections,
            PopularLectureCategory.NORMAL_CULTURE,
            items,
            10
        );
        given(getPopularLecturesUseCase.getInitPopularLectures(anyString(), anyInt(), anyInt(), any()))
            .willReturn(response);

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                .param("major", "컴퓨터공학")
                .param("entryYear", "2020"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sections", hasSize(1)))
            .andExpect(jsonPath("$.sections[0].categoryName").value("일반교양"))
            .andExpect(jsonPath("$.primeSection.categoryName").value("일반교양"))
            .andExpect(jsonPath("$.primeSection.lectures", hasSize(2)))
            .andExpect(jsonPath("$.primeSection.pageInfo.pageSize").value(10));

        then(getPopularLecturesUseCase).should(times(1))
            .getInitPopularLectures(anyString(), anyInt(), anyInt(), any());
    }

    @WithMockAuthenticationUser
    @DisplayName("카테고리 지정 시 해당 카테고리 페이지 응답한다.")
    @Test
    void getPopularLecturesByCategory_categorySelected() throws Exception {
        List<GetPopularLectureResponse> items = List.of(
            GetPopularLectureResponse.from(
                FindPopularLectureDto.of("LEC-20", "운영체제", 3, 80L, PopularLectureCategory.MANDATORY_MAJOR), 0.0),
            GetPopularLectureResponse.from(
                FindPopularLectureDto.of("LEC-21", "컴퓨터구조", 3, 70L, PopularLectureCategory.MANDATORY_MAJOR), 0.0)
        );
        PopularLecturesByCategoryResponse response = PopularLecturesByCategoryResponse.of(
            PopularLectureCategory.MANDATORY_MAJOR,
            items,
            5
        );
        given(getPopularLecturesUseCase.getPopularLecturesByCategory(anyString(), anyInt(), any(), anyInt(), any()))
            .willReturn(response);

        mockMvc.perform(get("/api/v1/lectures/popular/by-category")
                .param("major", "컴퓨터공학")
                .param("entryYear", "2020")
                .param("category", "MANDATORY_MAJOR")
                .param("limit", "5"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryName").value("전공필수"))
            .andExpect(jsonPath("$.lectures", hasSize(2)))
            .andExpect(jsonPath("$.pageInfo.pageSize").value(5));

        then(getPopularLecturesUseCase).should(times(1))
            .getPopularLecturesByCategory(anyString(), anyInt(), any(), anyInt(), any());
    }
}
