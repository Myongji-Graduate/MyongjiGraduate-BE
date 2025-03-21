package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Arrays;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DefaultGraduationRequirementType {
	// 단과대명|전공학점|공통교양학점|핵심교양학점|학문기초교양학점|일반교양학점|자유선택학점|전체학점|적용시작입학년도|적용마감입학년도
	HUMANITIES_16_17("인문대", 63, 15, 12, 12, 10, 16, 128, 16, 17),
	SOCIAL_SCIENCE_16_17("사회과학대", 63, 15, 12, 12, 10, 16, 128, 16, 17),
	BUSINESS_16_17("경영대", 63, 15, 12, 6, 10, 22, 128, 16, 17),
	LAW_16_17("법과대", 63, 15, 12, 9, 10, 19, 128, 16, 17),
	ICT_16_17("ICT융합대", 70, 15, 12, 18, 10, 9, 134, 16, 17),
	HUMANITIES_18_99("인문대", 63, 17, 12, 12, 10, 14, 128, 18, 99),
	SOCIAL_SCIENCE_18_99("사회과학대", 63, 17, 12, 12, 10, 14, 128, 18, 99),
	BUSINESS_18_24("경영대", 63, 17, 12, 6, 10, 20, 128, 18, 24),
	LAW_18_24("법과대", 63, 17, 12, 9, 10, 17, 128, 18, 24),
	ICT_18_24("ICT융합대", 70, 17, 12, 18, 10, 7, 134, 18, 24),
	//25년 이후 단과대
	MEDIA_HUMAN_LIFE("미디어·휴먼라이프대", 63, 17, 12, 12, 10, 14, 128, 18, 99),
	BUSINESS_25_99("경영대", 63, 17, 12, 9, 10, 17, 128, 25, 99),
	BUSINESS_GLOBAL_25_99("경영대", 63, 17, 12, 6, 10, 20, 128, 25, 99),
	ARTIFICIAL_INTELLIGENCE_SOFTWARE_25_99("인공지능·소프트웨어융합대학", 70, 17, 12, 15, 10, 10, 134, 25, 99);


	private final String collageName;
	private final int majorLectureCredit;
	private final int commonCultureCredit;
	private final int coreCultureCredit;
	private final int basicAcademicalLectureCredit;
	private final int normalLectureCredit;
	private final int freeElectiveLectureCredit;
	private final int totalCredit;
	private final int startEntryYear;
	private final int endEntryYear;

	public static DefaultGraduationRequirementType determineGraduationRequirement(
			College college,
			User user
	) {
		return Arrays.stream(DefaultGraduationRequirementType.values())
				.filter(gr -> gr.getCollageName().equals(college.getName()))
				.filter(gr -> {
					if (user.getPrimaryMajor().equals("글로벌비즈니스학전공")) {
						return gr.name().contains("BUSINESS_GLOBAL");
					}
					return !gr.name().contains("BUSINESS_GLOBAL");
				})
				.filter(gr -> gr.getStartEntryYear() <= user.getEntryYear()
						&& gr.getEndEntryYear() >= user.getEntryYear())
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException("일치하는 졸업 요건이 존재하지 않습니다."));
	}

	public GraduationRequirement convertToProfitGraduationRequirement(User user) {
		if (user.getStudentCategory() == StudentCategory.TRANSFER) {
			return createTransferGraduationRequirement(user);
		}
		return createDefaultGraduationRequirement(user);
	}

	private GraduationRequirement createDefaultGraduationRequirement(User user) {
		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
				.totalCredit(this.totalCredit)
				.primaryMajorCredit(this.majorLectureCredit)
				.dualMajorCredit(0)
				.subMajorCredit(0)
				.primaryBasicAcademicalCultureCredit(this.basicAcademicalLectureCredit)
				.dualBasicAcademicalCultureCredit(0)
				.commonCultureCredit(this.commonCultureCredit)
				.coreCultureCredit(this.coreCultureCredit)
				.normalCultureCredit(this.normalLectureCredit)
				.freeElectiveCredit(this.freeElectiveLectureCredit)
				.build();

		checkIsEnglishFreeUserAndTransferCredit(user, graduationRequirement);
		checkIsMultiMajorUserAndTransferCredit(user, graduationRequirement);

		return graduationRequirement;
	}

	private GraduationRequirement createTransferGraduationRequirement(User user) {
		College userCollege = College.findBelongingCollege(user.getPrimaryMajor(), user.getEntryYear());
		TransferGraduationRequirementType transferRequirement =
				TransferGraduationRequirementType.findByCollegeName(userCollege.getName());

		return GraduationRequirement.builder()
				.totalCredit(this.totalCredit)
				.primaryMajorCredit(this.majorLectureCredit)
				.normalCultureCredit(transferRequirement.getCombinedCultureCredit())
				.christianCredit(transferRequirement.getChristianCredit())
				.freeElectiveCredit(this.freeElectiveLectureCredit)
				.build();
	}

	private void checkIsEnglishFreeUserAndTransferCredit(
			User user,
			GraduationRequirement graduationRequirement
	) {
		if (user.getEnglishLevel() == EnglishLevel.FREE) {
			graduationRequirement.transferEnglishCreditCommonToNormal();
		}
	}

	private void checkIsMultiMajorUserAndTransferCredit(
			User user,
			GraduationRequirement graduationRequirement
	) {
		if (user.getStudentCategory() == StudentCategory.DUAL_MAJOR) {
			graduationRequirement.modifyCreditForDualMajor(user);
		}
		if (user.getStudentCategory() == StudentCategory.SUB_MAJOR) {
			graduationRequirement.modifyCreditForSubMajor();
		}
	}
}