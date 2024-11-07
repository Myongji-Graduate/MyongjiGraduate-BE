package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonCulture {

	private final Lecture lecture;
	private final CommonCultureCategory commonCultureCategory;

	@Builder
	private CommonCulture(Lecture lecture, CommonCultureCategory commonCultureCategory) {
		this.lecture = lecture;
		this.commonCultureCategory = commonCultureCategory;
	}

	public static CommonCulture of(Lecture lecture, CommonCultureCategory category) {
		return CommonCulture.builder()
			.lecture(lecture)
			.commonCultureCategory(category)
			.build();
	}
}
