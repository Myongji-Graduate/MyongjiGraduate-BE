package com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity.GraduationRequirementJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.out.LoadGraduationRequirementPort;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class LoadGraduationRequirementPersistenceAdapter implements LoadGraduationRequirementPort {

	private final GraduationRequirementRepository graduationRequirementRepository;
	private final GraduationRequirementMapper mapper;

	@Override
	public GraduationRequirement loadGraduationRequirement(User user) {
		if (isDualMajorUser(user)) {
			GraduationRequirementJpaEntity dualMajorRequirementEntity = graduationRequirementRepository.findDualMajorRequirementByUser(
				College.findBelongingCollege(user.getMajor()), user.getEntryYear());
			GraduationRequirement graduationRequirement = mapper.mapToDomainModel(dualMajorRequirementEntity);
			checkUserEnglishLevel(user, graduationRequirement);
			return graduationRequirement;
		}
		GraduationRequirementJpaEntity singleMajorRequirementEntity = graduationRequirementRepository.findSingleMajorRequirementByUser(
			College.findBelongingCollege(user.getMajor()), user.getEntryYear());
		GraduationRequirement graduationRequirement = mapper.mapToDomainModel(singleMajorRequirementEntity);
		checkUserEnglishLevel(user, graduationRequirement);
		return graduationRequirement;
	}

	private boolean isDualMajorUser(User user) {
		return user.getSubMajor() != null;
	}

	private void checkUserEnglishLevel(User user, GraduationRequirement graduationRequirement) {
		if (user.getEnglishLevel() == EnglishLevel.FREE) {
			graduationRequirement.transferEnglishCategoryCredit();
		}
	}
}
