package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonCulture implements Serializable {
	private static final long serialVersionUID = 1L;

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
