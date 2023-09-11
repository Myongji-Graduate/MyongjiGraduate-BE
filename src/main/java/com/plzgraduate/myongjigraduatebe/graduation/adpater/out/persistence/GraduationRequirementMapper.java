package com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity.GraduationRequirementJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;

@Component
public class GraduationRequirementMapper {

	public GraduationRequirement toModel(GraduationRequirementJpaEntity entity) {
		return GraduationRequirement.builder()
			.totalCredit(entity.getTotalCredit())
			.majorCredit(entity.getMajorCredit())
			.subMajorCredit(entity.getSubMajorCredit())
			.basicAcademicalCredit(entity.getBasicAcademicalCredit())
			.commonCultureCredit(entity.getCommonCultureCredit())
			.coreCultureCredit(entity.getCoreCultureCredit())
			.normalCultureCredit(entity.getNormalCultureCredit())
			.freeElectiveCredit(entity.getFreeElectiveCredit())
			.build();
	}
}
