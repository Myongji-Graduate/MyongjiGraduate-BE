package com.plzgraduate.myongjigraduatebe.lecturedetail.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase.SearchLectureReviewUseCase;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = SearchLectureReviewController.class)
class SearchLectureReviewControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean SearchLectureReviewUseCase searchLectureReviewUseCase;

    @Test
    @DisplayName("GET /api/v1/lecture-reviews : 기본 size=5, id DESC로 Slice 응답을 내려준다")
    void searchLectureReview_returnsSliceResponse() throws Exception {
        // given
        String subject = "성서와인간이해";
        String professor = "조내연";
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

        LectureReview r1 = LectureReview.of(subject, professor, "2025-1학기", BigDecimal.valueOf(5), "굿");
        Slice<LectureReview> slice = new SliceImpl<>(List.of(r1), pageable, true);

        when(searchLectureReviewUseCase.findLectureReviewBySubjectAndProfessor(eq(subject), eq(professor), any(Pageable.class)))
                .thenReturn(slice);

        // when & then
        mockMvc.perform(get("/api/v1/lecture-reviews")
                        .param("subject", subject)
                        .param("professor", professor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].subject", is(subject)))
                .andExpect(jsonPath("$.hasNext", is(true)))
                .andExpect(jsonPath("$.nextPage", is(1)));
    }
}