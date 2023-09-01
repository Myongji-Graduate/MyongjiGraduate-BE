package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
class ParsingStudentCategoryDto {
	private final String subMajor;
	private final String associatedMajor;
	private final StudentCategory studentCategory;

	@Builder
	private ParsingStudentCategoryDto(String subMajor, String associatedMajor, StudentCategory studentCategory) {
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
		this.studentCategory = studentCategory;
	}

	public static ParsingStudentCategoryDto of(String subMajor, String associatedMajor, StudentCategory studentCategory) {
		return ParsingStudentCategoryDto.builder()
			.subMajor(subMajor)
			.associatedMajor(associatedMajor)
			.studentCategory(studentCategory)
			.build();
	}
}
