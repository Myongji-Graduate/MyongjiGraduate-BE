package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationRequirement {

	private final int totalCredit;
	private final int majorCredit;
	private final int subMajorCredit;
	private final int basicAcademicalCredit;
	private int commonCultureCredit;
	private final int coreCultureCredit;
	private int normalCultureCredit;
	private int freeElectiveCredit;

	@Builder
	private GraduationRequirement(int totalCredit, int majorCredit, int subMajorCredit, int basicAcademicalCredit,
		int commonCultureCredit, int coreCultureCredit, int normalCultureCredit, int freeElectiveCredit) {
		this.totalCredit = totalCredit;
		this.majorCredit = majorCredit;
		this.subMajorCredit = subMajorCredit;
		this.basicAcademicalCredit = basicAcademicalCredit;
		this.commonCultureCredit = commonCultureCredit;
		this.coreCultureCredit = coreCultureCredit;
		this.normalCultureCredit = normalCultureCredit;
		this.freeElectiveCredit = freeElectiveCredit;
	}

	public void transferEnglishCreditCommonToNormal() {
		commonCultureCredit -= ENGLISH.getTotalCredit();
		normalCultureCredit += ENGLISH.getTotalCredit();
	}

	public void deleteFreeElectiveCredit() {
		freeElectiveCredit = 0;
	}
}
