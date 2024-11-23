package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import lombok.Builder;
import lombok.Getter;

@Getter
class ParsingStudentCategoryDto {

	private final String changeMajor;
	private final String dualMajor;
	private final String subMajor;
	private final String associatedMajor;
	private final StudentCategory studentCategory;
	private final TransferCredit transferCredit;

	@Builder
	private ParsingStudentCategoryDto(
		String changeMajor,
		String dualMajor,
		String subMajor,
		String associatedMajor,
		StudentCategory studentCategory,
		TransferCredit transferCredit
	) {
		this.changeMajor = changeMajor;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
		this.studentCategory = studentCategory;
		this.transferCredit = transferCredit;
	}

	public static ParsingStudentCategoryDto of(
		String changeMajor,
		String subMajor,
		String dualMajor,
		String associatedMajor,
		StudentCategory studentCategory,
		TransferCredit transferCredit
	) {
		return ParsingStudentCategoryDto.builder()
			.changeMajor(changeMajor)
			.dualMajor(dualMajor)
			.subMajor(subMajor)
			.associatedMajor(associatedMajor)
			.studentCategory(studentCategory)
			.transferCredit(transferCredit)
			.build();
	}
}
