package com.plzgraduate.myongjigraduatebe.graduation.dto;

import com.plzgraduate.myongjigraduatebe.lecture.dto.LectureResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class BachelorInfoCategory {
    private String categoryName;
    private List<LectureResponse> lectures;

    public static BachelorInfoCategory of(String categoryName, List<LectureResponse> lectures) {
        return new BachelorInfoCategory(categoryName, lectures);
    }
}
