package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LectureDetailMapperTest {

    private final LectureDetailMapper mapper = new LectureDetailMapper();

    @Test
    @DisplayName("LectureReviewJpaEntity → LectureReview 매핑")
    void mapToLectureReviewModel_success() {
        // given
        LectureReviewJpaEntity jpa = LectureReviewJpaEntity.builder()
                .id(1L)
                .subject("성서와인간이해")
                .professor("강안일")
                .semester("25-1")
                .rating(new BigDecimal("5.0"))
                .content("수업 깔끔")
                .build();

        // when
        LectureReview model = mapper.mapToLectureReviewModel(jpa);

        // then
        assertThat(model.getSubject()).isEqualTo("성서와인간이해");
        assertThat(model.getProfessor()).isEqualTo("강안일");
        assertThat(model.getSemester()).isEqualTo("25-1");
        assertThat(model.getRating()).isEqualByComparingTo("5.0");
        assertThat(model.getContent()).isEqualTo("수업 깔끔");
    }

    @Test
    @DisplayName("LectureInfoJpaEntity + 리뷰목록 → LectureInfo 매핑")
    void mapToLectureInfoModel_withReviews_success() {
        // given
        LectureInfoJpaEntity info = LectureInfoJpaEntity.builder()
                .id(10L)
                .subject("성서와인간이해")
                .professor("강안일")
                .assignment("없음")
                .attendance("출석 체크")
                .exam("한 번")
                .grading("보통")
                .teamwork("없음")
                .rating(new BigDecimal("5.0"))
                .build();

        LectureReviewJpaEntity r1 = LectureReviewJpaEntity.builder()
                .id(1L).subject("성서와인간이해").professor("강안일")
                .semester("25-1").rating(new BigDecimal("5")).content("좋음").build();

        // when
        LectureInfo model = mapper.mapToLectureInfoModel(info, List.of(r1));

        // then
        assertThat(model.getSubject()).isEqualTo("성서와인간이해");
        assertThat(model.getProfessor()).isEqualTo("강안일");
        assertThat(model.getAssignment()).isEqualTo("없음");
        assertThat(model.getAttendance()).isEqualTo("출석 체크");
        assertThat(model.getExam()).isEqualTo("한 번");
        assertThat(model.getGrading()).isEqualTo("보통");
        assertThat(model.getTeamwork()).isEqualTo("없음");
        assertThat(model.getRating()).isEqualByComparingTo("5.0");
        assertThat(model.getLectureReviews())
                .hasSize(1)
                .extracting(LectureReview::getContent)
                .containsExactly("좋음");
    }

    @Test
    @DisplayName("리뷰목록이 null이면 빈 리스트로 매핑")
    void mapToLectureInfoModel_withNullReviews_returnsEmptyList() {
        // given
        LectureInfoJpaEntity info = LectureInfoJpaEntity.builder()
                .id(11L)
                .subject("성서와인간이해")
                .professor("조내연")
                .assignment("없음")
                .attendance("두 번")
                .exam("보통")
                .grading("없음")
                .teamwork("없음")
                .rating(new BigDecimal("4.07"))
                .build();

        // when
        LectureInfo model = mapper.mapToLectureInfoModel(info, null);

        // then
        assertThat(model.getProfessor()).isEqualTo("조내연");
        assertThat(model.getLectureReviews()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("여러 LectureInfo 매핑: 일부 교수만 리뷰가 있어도 안전하게 매핑")
    void mapToLectureInfoModels_mixedReviews_success() {
        // given (입력 순서 보존 위해 LinkedHashMap 사용)
        LectureInfoJpaEntity ahn = LectureInfoJpaEntity.builder()
                .id(1L).subject("기초프로그래밍2").professor("최성운")
                .assignment("있음").attendance("매 수업").exam("두 번")
                .grading("상대").teamwork("없음").rating(new BigDecimal("4.5")).build();

        LectureInfoJpaEntity cho = LectureInfoJpaEntity.builder()
                .id(2L).subject("기초프로그래밍2").professor("정재희")
                .assignment("없음").attendance("두 번").exam("보통")
                .grading("없음").teamwork("없음").rating(new BigDecimal("4.0")).build();

        LectureReviewJpaEntity rv = LectureReviewJpaEntity.builder()
                .id(100L).subject("기초프로그래밍2").professor("최성운")
                .semester("24-2").rating(new BigDecimal("5")).content("최고").build();

        Map<String, List<LectureReviewJpaEntity>> previewsByProfessor = new LinkedHashMap<>();
        previewsByProfessor.put("최성운", List.of(rv));

        // when
        List<LectureInfo> models = mapper.mapToLectureInfoModels(
                List.of(ahn, cho),
                previewsByProfessor
        );

        // then
        assertThat(models).hasSize(2);

        LectureInfo first = models.get(0);
        assertThat(first.getProfessor()).isEqualTo("최성운");
        assertThat(first.getLectureReviews()).hasSize(1);
        assertThat(first.getLectureReviews().get(0).getContent()).isEqualTo("최고");

        LectureInfo second = models.get(1);
        assertThat(second.getProfessor()).isEqualTo("정재희");
        assertThat(second.getLectureReviews()).isEmpty();
    }
}