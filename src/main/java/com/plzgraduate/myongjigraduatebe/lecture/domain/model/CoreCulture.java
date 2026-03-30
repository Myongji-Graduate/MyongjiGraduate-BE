package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CoreCulture implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Lecture lecture;
	private final CoreCultureCategory coreCultureCategory;

	@Builder
	private CoreCulture(Lecture lecture, CoreCultureCategory coreCultureCategory) {
		this.lecture = lecture;
		this.coreCultureCategory = coreCultureCategory;
	}

	public static CoreCulture of(Lecture lecture, CoreCultureCategory category) {
		return CoreCulture.builder()
			.lecture(lecture)
			.coreCultureCategory(category)
			.build();
	}
}
