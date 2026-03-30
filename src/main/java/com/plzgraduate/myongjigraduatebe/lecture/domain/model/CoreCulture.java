package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class CoreCulture {

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
