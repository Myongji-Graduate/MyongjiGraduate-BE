package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationRequirement {

	private final int totalCredit;
	private int primaryMajorCredit;
	private int dualMajorCredit;
	private int subMajorCredit;
	private final int primaryBasicAcademicalCredit;
	private final int dualBasicAcademicalCredit;
	private int commonCultureCredit;
	private final int coreCultureCredit;
	private int normalCultureCredit;
	private int freeElectiveCredit;

	@Builder
	private GraduationRequirement(int totalCredit, int primaryMajorCredit, int dualMajorCredit, int subMajorCredit,
		int primaryBasicAcademicalCredit, int dualBasicAcademicalCredit, int commonCultureCredit, int coreCultureCredit,
		int normalCultureCredit, int freeElectiveCredit) {
		this.totalCredit = totalCredit;
		this.primaryMajorCredit = primaryMajorCredit;
		this.dualMajorCredit = dualMajorCredit;
		this.subMajorCredit = subMajorCredit;
		this.primaryBasicAcademicalCredit = primaryBasicAcademicalCredit;
		this.dualBasicAcademicalCredit = dualBasicAcademicalCredit;
		this.commonCultureCredit = commonCultureCredit;
		this.coreCultureCredit = coreCultureCredit;
		this.normalCultureCredit = normalCultureCredit;
		this.freeElectiveCredit = freeElectiveCredit;
	}

	public void transferEnglishCreditCommonToNormal() {
		commonCultureCredit -= ENGLISH.getTotalCredit();
		normalCultureCredit += ENGLISH.getTotalCredit();
	}

	public void modifyCreditForSubMajor() {
		subMajorCredit = 21;
		freeElectiveCredit = 0;
	}

	public void modifyCreditForDualMajor(int primaryMajorCredit, int dualMajorCredit) {
		//TODO: 복수 전공의 정확한 졸업요건 체크 후 로직 작성
	}
}
