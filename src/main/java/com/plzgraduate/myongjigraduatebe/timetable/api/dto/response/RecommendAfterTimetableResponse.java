package com.plzgraduate.myongjigraduatebe.timetable.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendAfterTimetableResponse {

    private final int semestersLeft;
    private final List<SemesterBlock> semesters;


    @Getter
    @Builder
    public static class SemesterBlock {
        private final String label;
        private final int creditTarget;
        private final List<LectureItem> lectures;
    }

    @Getter
    @Builder
    public static class LectureItem {
        private final String id;
        private final String name;
        private final int credit;
        private String category;
    }
}
