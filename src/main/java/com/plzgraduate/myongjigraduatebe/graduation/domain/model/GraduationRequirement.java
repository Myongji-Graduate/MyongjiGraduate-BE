package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationRequirement {

	private final int totalCredit;
	private final int majorCredit;
	private final int subMajorCredit;
	private final int basicAcademicalCredit;
	private final int commonCultureCredit;
	private final int coreCultureCredit;
	private final int normalCultureCredit;
	private final int freeElectiveCredit;
	private final StudentCategory studentCategory;

	@Builder
	private GraduationRequirement(int totalCredit, int majorCredit, int subMajorCredit, int basicAcademicalCredit,
		int commonCultureCredit, int coreCultureCredit, int normalCultureCredit, int freeElectiveCredit,
		StudentCategory studentCategory) {
		this.totalCredit = totalCredit;
		this.majorCredit = majorCredit;
		this.subMajorCredit = subMajorCredit;
		this.basicAcademicalCredit = basicAcademicalCredit;
		this.commonCultureCredit = commonCultureCredit;
		this.coreCultureCredit = coreCultureCredit;
		this.normalCultureCredit = normalCultureCredit;
		this.freeElectiveCredit = freeElectiveCredit;
		this.studentCategory = studentCategory;
	}
}
