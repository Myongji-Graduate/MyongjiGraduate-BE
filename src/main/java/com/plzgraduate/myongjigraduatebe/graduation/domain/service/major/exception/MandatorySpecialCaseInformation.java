package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class MandatorySpecialCaseInformation {

	private final boolean completeMandatorySpecialCase;

	private final int removedMandatoryTotalCredit;

	public static MandatorySpecialCaseInformation of(boolean completeMandatorySpecialCase, int removedMandatoryTotalCredit) {
		return MandatorySpecialCaseInformation.builder()
			.completeMandatorySpecialCase(completeMandatorySpecialCase)
			.removedMandatoryTotalCredit(removedMandatoryTotalCredit)
			.build();
	}
}
