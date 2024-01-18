package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Arrays;
import java.util.NoSuchElementException;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GraduationRequirementType {
	// 단과대명|전공학점|공통교양학점|핵심교양학점|학문기초교양학점|일반교양학점|자유선택학점|전체학점|적용시작입학년도|적용마감입학년도
	HUMANITIES_16_17("인문대", 63, 15, 12, 12, 10, 16, 128, 16, 17),
	SOCIAL_SCIENCE_16_17("사회과학대", 63, 15, 12, 12, 10, 16, 128, 16, 17),
	BUSINESS_16_17("경영대", 63, 15, 12, 6, 10, 22, 128, 16, 17),
	LAW_16_17("법과대", 63, 15, 12, 9, 10, 19, 128, 16, 17),
	ICT_16_17("ICT융합대", 70, 15, 12, 18, 10, 9, 134, 16, 17),
	HUMANITIES_18_99("인문대", 63, 17, 12, 12, 10, 14, 128, 18, 99),
	SOCIAL_SCIENCE_18_99("사회과학대", 63, 17, 12, 12, 19, 14, 128, 18, 99),
	BUSINESS_18_99("경영대", 63, 17, 12, 6, 10, 20, 128, 18, 99),
	LAW_18_99("법과대", 63, 17, 12, 9, 10, 17, 128, 18, 99),
	ICT_18_99("ICT융합대", 70, 17, 12, 18, 10, 7, 134, 18, 99);

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

	public static GraduationRequirementType determineGraduationRequirement(College college, User user) {
		return Arrays.stream(GraduationRequirementType.values())
			.filter(gr -> gr.getCollageName().equals(college.getName()))
			.filter(gr -> gr.getStartEntryYear() <= user.getEntryYear()&& gr.getEndEntryYear() >= user.getEntryYear())
			.findFirst()
			.orElseThrow(() -> new NoSuchElementException("일치하는 졸업 요건이 존재하지 않습니다."));
	}

	public GraduationRequirement convertToProfitGraduationRequirement(User user) {
		GraduationRequirement graduationRequirement = GraduationRequirement.builder()
			.totalCredit(this.totalCredit)
			.majorCredit(this.majorLectureCredit)
			.basicAcademicalCredit(this.basicAcademicalLectureCredit)
			.commonCultureCredit(this.commonCultureCredit)
			.coreCultureCredit(this.coreCultureCredit)
			.normalCultureCredit(this.normalLectureCredit)
			.freeElectiveCredit(this.freeElectiveLectureCredit).build();

		checkUserEnglishLevel(user, graduationRequirement);
		//TODO: Additional Major에 따른 졸업요건 변화 체크 후 졸업 요건 학점 변화 적용
		return graduationRequirement;
	}

	private void checkUserEnglishLevel(User user, GraduationRequirement graduationRequirement) {
		if (user.getEnglishLevel() == EnglishLevel.FREE) {
			graduationRequirement.transferEnglishCreditCommonToNormal();
		}
	}
}
