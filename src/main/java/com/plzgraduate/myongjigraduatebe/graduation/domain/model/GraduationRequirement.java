package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.DualMajorGraduationRequirementType.findBelongingDualMajorGraduationRequirementType;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.ENGLISH;

import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationRequirement {

	private final int totalCredit;
	private final int primaryBasicAcademicalCultureCredit;
	private final int coreCultureCredit;
	private int primaryMajorCredit;
	private int dualMajorCredit;
	private int subMajorCredit;
	private int dualBasicAcademicalCultureCredit;
	private int commonCultureCredit;
	private int normalCultureCredit;
	private int freeElectiveCredit;

	@Builder
	private GraduationRequirement(
		int totalCredit, int primaryMajorCredit, int dualMajorCredit,
		int subMajorCredit,
		int primaryBasicAcademicalCultureCredit, int dualBasicAcademicalCultureCredit,
		int commonCultureCredit,
		int coreCultureCredit,
		int normalCultureCredit, int freeElectiveCredit
	) {
		this.totalCredit = totalCredit;
		this.primaryMajorCredit = primaryMajorCredit;
		this.dualMajorCredit = dualMajorCredit;
		this.subMajorCredit = subMajorCredit;
		this.primaryBasicAcademicalCultureCredit = primaryBasicAcademicalCultureCredit;
		this.dualBasicAcademicalCultureCredit = dualBasicAcademicalCultureCredit;
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

	public void modifyCreditForDualMajor(User user) {
		DualMajorGraduationRequirementType primaryMajorGraduationRequirementType =
			findBelongingDualMajorGraduationRequirementType(
				College.findBelongingCollege(user.getPrimaryMajor())
					.getName());
		DualMajorGraduationRequirementType dualMajorGraduationRequirementType =
			findBelongingDualMajorGraduationRequirementType(
				College.findBelongingCollege(user.getDualMajor())
					.getName());

		primaryMajorCredit = primaryMajorGraduationRequirementType.getMajorCredit();
		dualMajorCredit = dualMajorGraduationRequirementType.getMajorCredit();
		dualBasicAcademicalCultureCredit = dualMajorGraduationRequirementType.getBasicAcademicalCultureCredit();
		normalCultureCredit = 0;
		freeElectiveCredit = calculateFreeElectiveCreditWithDualMajorStudent();
	}

	private int calculateFreeElectiveCreditWithDualMajorStudent() {
		int freeElectiveCredit =
			totalCredit - commonCultureCredit - coreCultureCredit - primaryMajorCredit
				- dualMajorCredit
				- primaryBasicAcademicalCultureCredit - dualBasicAcademicalCultureCredit;
		return Math.max(freeElectiveCredit, 0);
	}

	public int getBasicCreditByMajorType(MajorType majorType) {
		if (majorType == MajorType.PRIMARY) {
			return primaryBasicAcademicalCultureCredit;
		}
		return dualBasicAcademicalCultureCredit;
	}

	public int getMajorCreditByMajorType(MajorType majorType) {
		if (majorType == MajorType.PRIMARY) {
			return primaryMajorCredit;
		} else if (majorType == MajorType.DUAL) {
			return dualMajorCredit;
		}
		return subMajorCredit;
	}
}
