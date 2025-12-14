package com.plzgraduate.myongjigraduatebe.lecturedetail.application.service;

import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.SearchLectureReviewPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase.SearchLectureReviewUseCase;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SearchLectureReviewServiceTest {

    private SearchLectureReviewPort port;
    private SearchLectureReviewUseCase service;

    @BeforeEach
    void setUp() {
        port = mock(SearchLectureReviewPort.class);
        service = new SearchLectureReviewService(port);
    }

    @Test
    @DisplayName("subject/professor/pageable로 Port를 호출하고 Slice를 그대로 반환한다")
    void findLectureReviewBySubjectAndProfessor_delegatesToPort() {
        // given
        String subject = "성서와인간이해";
        String professor = "조내연";
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

        LectureReview r = LectureReview.of(subject, professor, "2025-1학기", BigDecimal.valueOf(5), "굿");
        Slice<LectureReview> slice = new SliceImpl<>(List.of(r), pageable, false);

        when(port.findBySubjectAndProfessor(subject, professor, pageable)).thenReturn(slice);

        // when
        Slice<LectureReview> result = service.findLectureReviewBySubjectAndProfessor(subject, professor, pageable);

        // then: Port 호출 검증
        ArgumentCaptor<String> subCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> profCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pgCap = ArgumentCaptor.forClass(Pageable.class);
        verify(port).findBySubjectAndProfessor(subCap.capture(), profCap.capture(), pgCap.capture());
        assertThat(subCap.getValue()).isEqualTo(subject);
        assertThat(profCap.getValue()).isEqualTo(professor);
        assertThat(pgCap.getValue().getPageSize()).isEqualTo(5);
        assertThat(Objects.requireNonNull(pgCap.getValue().getSort().getOrderFor("id")).getDirection()).isEqualTo(Sort.Direction.DESC);

        // 반환 검증
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.isLast()).isTrue();
    }
}