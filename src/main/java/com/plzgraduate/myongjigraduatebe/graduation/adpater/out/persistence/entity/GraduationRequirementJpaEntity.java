package com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.plzgraduate.myongjigraduatebe.user.domain.model.College;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "graduation_requirement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GraduationRequirementJpaEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int totalCredit;

	private int majorCredit;

	private int subMajorCredit;

	private int basicAcademicalCredit;

	private int commonCultureCredit;

	private int coreCultureCredit;

	private int normalCultureCredit;

	private int freeElectiveCredit;

	@Enumerated(value = EnumType.STRING)
	private College college;

	private int startEntryYear;

	private int endEntryYear;

	@Builder
	private GraduationRequirementJpaEntity(Long id, int totalCredit, int majorCredit, int subMajorCredit,
		int basicAcademicalCredit, int commonCultureCredit, int coreCultureCredit, int normalCultureCredit,
		int freeElectiveCredit, College college, int startEntryYear, int endEntryYear) {
		this.id = id;
		this.totalCredit = totalCredit;
		this.majorCredit = majorCredit;
		this.subMajorCredit = subMajorCredit;
		this.basicAcademicalCredit = basicAcademicalCredit;
		this.commonCultureCredit = commonCultureCredit;
		this.coreCultureCredit = coreCultureCredit;
		this.normalCultureCredit = normalCultureCredit;
		this.freeElectiveCredit = freeElectiveCredit;
		this.college = college;
		this.startEntryYear = startEntryYear;
		this.endEntryYear = endEntryYear;
	}
}
