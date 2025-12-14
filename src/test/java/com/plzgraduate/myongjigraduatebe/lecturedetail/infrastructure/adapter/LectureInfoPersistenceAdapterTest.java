package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper.LectureDetailMapper;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureInfoRepository;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureInfoPersistenceAdapterTest {

    private LectureInfoRepository lectureInfoRepository;
    private LectureReviewRepository lectureReviewRepository;
    private LectureDetailMapper mapper;
    private LectureInfoPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        lectureInfoRepository = mock(LectureInfoRepository.class);
        lectureReviewRepository = mock(LectureReviewRepository.class);
        mapper = mock(LectureDetailMapper.class);
        adapter = new LectureInfoPersistenceAdapter(lectureInfoRepository, lectureReviewRepository, mapper);
    }

    @Test
    @DisplayName("과목명으로 조회 시 교수별로 리뷰 Top5를 조회하고, 매퍼에 올바른 맵을 전달한다")
    void findBySubject_returnsListWithProfessorPreviewLookup() {
        // given
        String subject = "성서와인간이해";

        LectureInfoJpaEntity a = LectureInfoJpaEntity.builder()
                .id(1L).subject(subject).professor("강안일")
                .assignment(null).attendance(null).exam(null)
                .grading(null).teamwork(null).rating(new BigDecimal("5.0"))
                .build();

        LectureInfoJpaEntity b = LectureInfoJpaEntity.builder()
                .id(2L).subject(subject).professor("조내연")
                .assignment("없음").attendance(null).exam("두 번")
                .grading("보통").teamwork("없음").rating(new BigDecimal("4.07"))
                .build();

        List<LectureInfoJpaEntity> infoEntities = List.of(a, b);
        given(lectureInfoRepository.findAllBySubject(subject)).willReturn(infoEntities);

        List<LectureReviewJpaEntity> reviewsA = List.of(
                LectureReviewJpaEntity.builder().id(101L).subject(subject).professor("강안일").content("A-1").build(),
                LectureReviewJpaEntity.builder().id(102L).subject(subject).professor("강안일").content("A-2").build(),
                LectureReviewJpaEntity.builder().id(103L).subject(subject).professor("강안일").content("A-3").build()
        );
        List<LectureReviewJpaEntity> reviewsB = List.of(
                LectureReviewJpaEntity.builder().id(201L).subject(subject).professor("조내연").content("B-1").build(),
                LectureReviewJpaEntity.builder().id(202L).subject(subject).professor("조내연").content("B-2").build(),
                LectureReviewJpaEntity.builder().id(203L).subject(subject).professor("조내연").content("B-3").build()
        );

        given(lectureReviewRepository.findTop5BySubjectAndProfessorOrderByIdDesc(subject, "강안일"))
                .willReturn(reviewsA);
        given(lectureReviewRepository.findTop5BySubjectAndProfessorOrderByIdDesc(subject, "조내연"))
                .willReturn(reviewsB);

        List<LectureInfo> expected = List.of(
                LectureInfo.builder().subject(subject).professor("강안일").rating(new BigDecimal("5.0")).build(),
                LectureInfo.builder().subject(subject).professor("조내연").rating(new BigDecimal("4.07")).build()
        );
        given(mapper.mapToLectureInfoModels(eq(infoEntities), anyMap())).willReturn(expected);

        // when
        List<LectureInfo> result = adapter.findBySubject(subject);

        // then
        assertThat(result).isEqualTo(expected);

        verify(lectureInfoRepository).findAllBySubject(subject);
        verify(lectureReviewRepository).findTop5BySubjectAndProfessorOrderByIdDesc(subject, "강안일");
        verify(lectureReviewRepository).findTop5BySubjectAndProfessorOrderByIdDesc(subject, "조내연");

        ArgumentCaptor<Map<String, List<LectureReviewJpaEntity>>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mapper).mapToLectureInfoModels(eq(infoEntities), mapCaptor.capture());

        Map<String, List<LectureReviewJpaEntity>> captured = mapCaptor.getValue();
        assertThat(captured).containsOnlyKeys("강안일", "조내연");
        assertThat(captured.get("강안일")).extracting("id").containsExactly(101L, 102L, 103L);
        assertThat(captured.get("조내연")).extracting("id").containsExactly(201L, 202L, 203L);

        verifyNoMoreInteractions(lectureInfoRepository, lectureReviewRepository, mapper);
    }
}