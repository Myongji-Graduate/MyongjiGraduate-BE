package com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity.GraduationRequirementJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.out.LoadGraduationRequirementPort;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class LoadGraduationRequirementPersistenceAdapter implements LoadGraduationRequirementPort {

	private final GraduationRequirementRepository graduationRequirementRepository;

	@Override
	public GraduationRequirement loadGraduationRequirement(User user) {
		if (isDualMajorUser(user)) {
			GraduationRequirementJpaEntity dualMajorRequirementEntity = graduationRequirementRepository.findDualMajorRequirementByUser(
				College.findBelongingCollege(user.getMajor()), user.getEntryYear());
			return toModel(dualMajorRequirementEntity);
		}
		GraduationRequirementJpaEntity singleMajorRequirementEntity = graduationRequirementRepository.findSingleMajorRequirementByUser(
			College.findBelongingCollege(user.getMajor()), user.getEntryYear());
		return toModel(singleMajorRequirementEntity);
	}

	private boolean isDualMajorUser(User user) {
		return user.getSubMajor() != null;
	}

	private GraduationRequirement toModel(GraduationRequirementJpaEntity entity) {
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
