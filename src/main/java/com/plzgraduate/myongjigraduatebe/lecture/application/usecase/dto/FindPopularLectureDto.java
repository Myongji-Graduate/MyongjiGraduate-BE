package com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindPopularLectureDto {
    private final String lectureId;
    private final String lectureName;
    private final int credit;
    private final long totalCount;
    private final PopularLectureCategory categoryName;

    @QueryProjection
    @Builder
    public FindPopularLectureDto(String lectureId, String lectureName, int credit, long totalCount, PopularLectureCategory categoryName) {
        this.lectureId = lectureId;
        this.lectureName = lectureName;
        this.credit = credit;
        this.totalCount = totalCount;
        this.categoryName = categoryName;
    }

    @QueryProjection
    public FindPopularLectureDto(String lectureId, String lectureName, int credit, long totalCount) {
        this(lectureId, lectureName, credit, totalCount, null);
    }

    public static FindPopularLectureDto of(String lectureId, String lectureName, int credit, long totalCount, PopularLectureCategory categoryName) {
        return FindPopularLectureDto.builder()
                .lectureId(lectureId)
                .lectureName(lectureName)
                .credit(credit)
                .totalCount(totalCount)
                .categoryName(categoryName)
                .build();
    }
}
