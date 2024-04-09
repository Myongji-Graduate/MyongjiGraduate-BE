package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCommonCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DefaultGraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture.CommonCultureGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateCommonCultureGraduationService implements CalculateCommonCultureGraduationUseCase {

	private final FindCommonCulturePort findCommonCulturePort;

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory) {
		Set<CommonCulture> graduationCommonCultures = findCommonCulturePort.findCommonCulture(user);
		GraduationManager<CommonCulture> commonCultureGraduationManager = new CommonCultureGraduationManager();
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);
		return commonCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCommonCultures, graduationRequirement.getCommonCultureCredit());
	}

	private GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user.getPrimaryMajor());
		DefaultGraduationRequirementType defaultGraduationRequirement = DefaultGraduationRequirementType.determineGraduationRequirement(
			userCollage, user);
		return defaultGraduationRequirement.convertToProfitGraduationRequirement(user);
	}
}
