package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper.LectureDetailMapper;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LectureInfoPersistenceAdapterTest {

    private LectureInfoRepository repository;
    private LectureInfoPersistenceAdapter adapter;
    private LectureDetailMapper mapper;


    @BeforeEach
    void setUp() {
        repository = mock(LectureInfoRepository.class);
        mapper = mock(LectureDetailMapper.class);
        adapter = new LectureInfoPersistenceAdapter(repository, mapper);
    }


    @Test
    @DisplayName("Repository 엔티티를 도메인 모델(단일)로 매핑하여 반환한다")
    void findBySubjectAndProfessor_returnsDomain() {
        // given
        String subject = "성서와인간이해";
        String professor = "조내연";

        LectureInfoJpaEntity entity = LectureInfoJpaEntity.builder()
                .id(6L)
                .subject(subject)
                .professor(professor)
                .assignment("없음")
                .attendance("두 번")
                .exam("보통")
                .grading("없음")
                .teamwork("없음")
                .rating(new BigDecimal("4.07"))
                .build();

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

        given(repository.findBySubjectAndProfessor(eq(subject), professor))
                .willReturn(entity);
        given(mapper.mapToLectureInfoModel(entity)).willReturn(info);

        // when
        LectureInfo result = adapter.findBySubjectAndProfessor(subject, professor);

        // then
        assertThat(result.getSubject()).isEqualTo(subject);
        assertThat(result.getProfessor()).isEqualTo(professor);
        assertThat(result.getAssignment()).isEqualTo("없음");
        assertThat(result.getAttendance()).isEqualTo("두 번");
        assertThat(result.getExam()).isEqualTo("보통");
        assertThat(result.getGrading()).isEqualTo("없음");
        assertThat(result.getTeamwork()).isEqualTo("없음");
        assertThat(result.getRating()).isEqualByComparingTo("4.07");

        verify(repository).findBySubjectAndProfessor(subject, professor);
    }
}