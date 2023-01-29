package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class BachelorInfoLecturesResponse {

    private List<BachelorInfoCategory> major;
    private List<BachelorInfoCategory> commonCulture;
    private List<BachelorInfoCategory> coreCulture;
    private List<BachelorInfoCategory> basicAcademicalCulture;

    public static BachelorInfoLecturesResponse of(
            List<BachelorInfoCategory> major,
            List<BachelorInfoCategory> commonCulture,
            List<BachelorInfoCategory> coreCulture,
            List<BachelorInfoCategory> basicAcademicalCulture){
        return BachelorInfoLecturesResponse.builder()
                .major(major)
                .commonCulture(commonCulture)
                .coreCulture(coreCulture)
                .basicAcademicalCulture(basicAcademicalCulture)
                .build();
    }

}
