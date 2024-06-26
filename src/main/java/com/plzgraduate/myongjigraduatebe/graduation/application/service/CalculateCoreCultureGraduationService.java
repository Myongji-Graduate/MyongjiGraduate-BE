package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture.CoreGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateCoreCultureGraduationService implements CalculateDetailGraduationUseCase {

	private final FindCoreCulturePort findCoreCulturePort;
	private final CoreGraduationManager coreCultureGraduationManager;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == CORE_CULTURE;
	}

	@Override
	public DetailGraduationResult calculateSingleDetailGraduation(User user, GraduationCategory graduationCategory, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<CoreCulture> graduationCoreCultures = findCoreCulturePort.findCoreCulture(user);
		return coreCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCoreCultures, graduationRequirement.getCoreCultureCredit());
	}
}
