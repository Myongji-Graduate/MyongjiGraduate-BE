package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class MandatorySpecialCaseInformation {

	private final boolean isCompleteMandatorySpecialCase;

	private final int removedMandatoryTotalCredit;

	public static MandatorySpecialCaseInformation of(boolean completeMandatorySpecialCase, int removedMandatoryTotalCredit) {
		return MandatorySpecialCaseInformation.builder()
			.isCompleteMandatorySpecialCase(completeMandatorySpecialCase)
			.removedMandatoryTotalCredit(removedMandatoryTotalCredit)
			.build();
	}
}
