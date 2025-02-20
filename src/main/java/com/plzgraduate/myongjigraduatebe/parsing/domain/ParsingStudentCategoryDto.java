package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
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
	private final ExchangeCredit exchangeCredit;


	@Builder
	private ParsingStudentCategoryDto(
			String changeMajor,
			String dualMajor,
			String subMajor,
			String associatedMajor,
			StudentCategory studentCategory,
			TransferCredit transferCredit,
			ExchangeCredit exchangeCredit
	) {
		this.changeMajor = changeMajor;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.associatedMajor = associatedMajor;
		this.studentCategory = studentCategory;
		this.transferCredit = transferCredit;
		this.exchangeCredit = exchangeCredit;
	}

	public static ParsingStudentCategoryDto of(
			String changeMajor,
			String subMajor,
			String dualMajor,
			String associatedMajor,
			StudentCategory studentCategory,
			TransferCredit transferCredit,
			ExchangeCredit exchangeCredit
	) {
		return ParsingStudentCategoryDto.builder()
				.changeMajor(changeMajor)
				.dualMajor(dualMajor)
				.subMajor(subMajor)
				.associatedMajor(associatedMajor)
				.studentCategory(studentCategory)
				.transferCredit(transferCredit)
				.exchangeCredit(exchangeCredit)
				.build();
	}
}
