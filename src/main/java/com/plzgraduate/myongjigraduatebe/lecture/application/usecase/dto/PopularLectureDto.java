package com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PopularLectureDto {
    private final String lectureId;
    private final String lectureName;
    private final int credit;
    private final long totalCount;
    private final PopularLectureCategory categoryName;
    private final double averageRating;

    public static PopularLectureDto withCategory(PopularLectureDto base, PopularLectureCategory categoryName) {
        return base.toBuilder().categoryName(categoryName).build();
    }

    public static PopularLectureDto ofWithAverage(String lectureId, String lectureName, int credit, long totalCount, PopularLectureCategory categoryName, double averageRating) {
        return PopularLectureDto.builder()
                .lectureId(lectureId)
                .lectureName(lectureName)
                .credit(credit)
                .totalCount(totalCount)
                .categoryName(categoryName)
                .averageRating(averageRating)
                .build();
    }
}
