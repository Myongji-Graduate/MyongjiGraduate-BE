package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper.LectureDetailMapper;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.repository.LectureReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LectureReviewPersistenceAdapterTest {

    private LectureReviewRepository repository;
    private LectureDetailMapper mapper;
    private LectureReviewPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(LectureReviewRepository.class);
        mapper = mock(LectureDetailMapper.class);
        adapter = new LectureReviewPersistenceAdapter(repository, mapper);
    }

    @Test
    @DisplayName("Repository Slice를 받아 Domain Slice로 매핑해서 반환한다")
    void findBySubjectAndProfessor() {
        // given
        String subject = "성서와인간이해";
        String professor = "조내연";
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));

        LectureReviewJpaEntity entity = LectureReviewJpaEntity.builder()
                .id(10L)
                .subject(subject)
                .professor(professor)
                .semester("2025-1학기")
                .rating(BigDecimal.valueOf(5))
                .content("굿")
                .build();

        Slice<LectureReviewJpaEntity> jpaSlice = new SliceImpl<>(List.of(entity), pageable, true);

        when(repository.findBySubjectAndProfessor(subject, professor, pageable)).thenReturn(jpaSlice);

        LectureReview domain = LectureReview.of(subject, professor, "2025-1학기", BigDecimal.valueOf(5), "굿");
        when(mapper.mapToLectureReviewModel(entity)).thenReturn(domain);

        // when
        Slice<LectureReview> result = adapter.findBySubjectAndProfessor(subject, professor, pageable);

        // then
        verify(repository).findBySubjectAndProfessor(subject, professor, pageable);
        verify(mapper).mapToLectureReviewModel(entity);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.hasNext()).isTrue();
    }
}