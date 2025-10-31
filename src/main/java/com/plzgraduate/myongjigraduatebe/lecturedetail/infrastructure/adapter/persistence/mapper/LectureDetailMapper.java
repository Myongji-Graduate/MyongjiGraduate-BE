package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;
import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureInfoJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LectureDetailMapper {

    public LectureReview mapToLectureReviewModel(LectureReviewJpaEntity lectureReview) {
        return LectureReview.builder()
                .subject(lectureReview.getSubject())
                .professor(lectureReview.getProfessor())
                .semester(lectureReview.getSemester())
                .rating(lectureReview.getRating())
                .content(lectureReview.getContent())
                .build();
    }

    public LectureInfo mapToLectureInfoModel(
            LectureInfoJpaEntity lectureInfo,
            List<LectureReviewJpaEntity> lectureReviews
    ) {
        List<LectureReview> previews = lectureReviews == null ? List.of() :
                lectureReviews.stream()
                        .map(this::mapToLectureReviewModel)
                        .collect(Collectors.toList());

        return LectureInfo.builder()
                .subject(lectureInfo.getSubject())
                .professor(lectureInfo.getProfessor())
                .assignment(lectureInfo.getAssignment())
                .attendance(lectureInfo.getAttendance())
                .exam(lectureInfo.getExam())
                .grading(lectureInfo.getGrading())
                .teamwork(lectureInfo.getTeamwork())
                .rating(lectureInfo.getRating())
                .lectureReviews(previews)
                .build();
    }

    public List<LectureInfo> mapToLectureInfoModels(
            List<LectureInfoJpaEntity> lectureInfos,
            Map<String, List<LectureReviewJpaEntity>> previewsByProfessor
    ) {
        return lectureInfos.stream()
                .map(info -> {
                    List<LectureReviewJpaEntity> previews =
                            previewsByProfessor.getOrDefault(info.getProfessor(), List.of());
                    return mapToLectureInfoModel(info, previews);
                })
                .collect(Collectors.toList());
    }
}