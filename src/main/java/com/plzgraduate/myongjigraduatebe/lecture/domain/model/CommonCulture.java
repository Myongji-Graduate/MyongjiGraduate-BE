package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
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

	@Override
	public String toString() {
		return "CommonCulture{" +
			"lecture=" + lecture +
			", commonCultureCategory=" + commonCultureCategory +
			'}';
	}
}
