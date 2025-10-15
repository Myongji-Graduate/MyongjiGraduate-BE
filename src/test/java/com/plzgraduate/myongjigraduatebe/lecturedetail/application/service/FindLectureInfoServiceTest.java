package com.plzgraduate.myongjigraduatebe.lecturedetail.application.service;

import com.plzgraduate.myongjigraduatebe.lecturedetail.application.port.FindLectureInfoPort;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

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
    @DisplayName("subject & professor로 LectureInfo 조회")
    void findLectureInfoBySubjectAndProfessor_returnsLectureInfo() {
        // given
        String subject = "성서와인간이해";
        String professor = "조내연";

        LectureInfo info = LectureInfo.builder()
                .subject(subject)
                .professor(professor)
                .assignment("없음")
                .attendance("두 번")
                .exam("보통")
                .grading("없음")
                .teamwork("없음")
                .rating(new BigDecimal("4.07"))
                .build();

        given(findLectureInfoPort.findBySubjectAndProfessor(subject, professor))
                .willReturn(info);

        // when
        LectureInfo result = service.findLectureInfoBySubjectAndProfessor(subject, professor);

        // then
        assertThat(result.getSubject()).isEqualTo(subject);
        assertThat(result.getProfessor()).isEqualTo(professor);
        assertThat(result.getAssignment()).isEqualTo("없음");
        assertThat(result.getAttendance()).isEqualTo("두 번");
        assertThat(result.getExam()).isEqualTo("보통");
        assertThat(result.getGrading()).isEqualTo("없음");
        assertThat(result.getTeamwork()).isEqualTo("없음");
        assertThat(result.getRating()).isEqualByComparingTo("4.07");

        verify(findLectureInfoPort).findBySubjectAndProfessor(subject, professor);
    }
}