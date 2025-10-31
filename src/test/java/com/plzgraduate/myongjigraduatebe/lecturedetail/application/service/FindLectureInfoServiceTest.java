package com.plzgraduate.myongjigraduatebe.lecturedetail.application.service;

import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.FindLectureInfoPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FindLectureInfoServiceTest {

    @Mock
    private FindLectureInfoPort findLectureInfoPort;

    @InjectMocks
    private FindLectureInfoService service;

    @Test
    @DisplayName("과목명으로 교수별 LectureInfo 목록을 조회한다(리뷰 프리뷰 포함)")
    void findLectureInfoBySubject_returnsLectureInfoList() {
        // given
        String subject = "성서와인간이해";

        LectureInfo professor1 = LectureInfo.builder()
                .subject(subject)
                .professor("강안일")
                .assignment("없음")
                .attendance("출석 체크")
                .exam("한 번")
                .grading("보통")
                .teamwork("없음")
                .rating(new BigDecimal("5.0"))
                .lectureReviews(List.of(
                        LectureReview.builder()
                                .subject(subject)
                                .professor("강안일")
                                .semester("25년 1학기 수강자")
                                .rating(BigDecimal.valueOf(5))
                                .content("수업 깔끔")
                                .build()
                ))
                .build();

        LectureInfo professor2 = LectureInfo.builder()
                .subject(subject)
                .professor("조내연")
                .assignment("없음")
                .attendance("두 번")
                .exam("보통")
                .grading("없음")
                .teamwork("없음")
                .rating(new BigDecimal("4.07"))
                .lectureReviews(List.of())
                .build();

        given(findLectureInfoPort.findBySubject(subject))
                .willReturn(List.of(professor1, professor2));

        // when
        List<LectureInfo> result = service.findLectureInfoBySubject(subject);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSubject()).isEqualTo(subject);
        assertThat(result.get(0).getProfessor()).isEqualTo("강안일");
        assertThat(result.get(0).getLectureReviews()).isNotNull();

        assertThat(result.get(1).getProfessor()).isEqualTo("조내연");
        assertThat(result.get(1).getRating()).isEqualByComparingTo("4.07");

        verify(findLectureInfoPort).findBySubject(subject);
    }

    @Test
    @DisplayName("해당 과목의 강좌 정보가 없으면 빈 리스트 반환")
    void findLectureInfoBySubject_returnsEmptyListWhenNoData() {
        // given
        String subject = "없는과목";
        given(findLectureInfoPort.findBySubject(subject))
                .willReturn(List.of());

        // when
        List<LectureInfo> result = service.findLectureInfoBySubject(subject);

        // then
        assertThat(result).isEmpty();
        verify(findLectureInfoPort).findBySubject(subject);
    }
}