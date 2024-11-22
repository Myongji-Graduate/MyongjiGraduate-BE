package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
class ParsingStudentCategoryDto {

	private final String changeMajor;
	private final String dualMajor;
	private final String subMajor;
	private final String associatedMajor;
	private final String transferStatus;
	private final StudentCategory studentCategory;

	@Builder
	private ParsingStudentCategoryDto(String changeMajor, String dualMajor, String subMajor,
                                      String associatedMajor, String transferStatus,
                                      StudentCategory studentCategory) {
		this.changeMajor = changeMajor;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
        this.transferStatus = transferStatus;
        this.studentCategory = studentCategory;
	}

	public static ParsingStudentCategoryDto of(String changeMajor, String subMajor,
		String dualMajor,
		String associatedMajor, String transferStatus, StudentCategory studentCategory) {
		return ParsingStudentCategoryDto.builder()
			.changeMajor(changeMajor)
			.dualMajor(dualMajor)
			.subMajor(subMajor)
			.associatedMajor(associatedMajor)
			.transferStatus(transferStatus)
			.studentCategory(studentCategory)
			.build();
	}
}
