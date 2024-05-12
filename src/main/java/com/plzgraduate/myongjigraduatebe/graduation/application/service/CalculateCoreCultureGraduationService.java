package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCoreCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture.CommonCultureGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture.CoreCultureGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateCoreCultureGraduationService
	implements CalculateCoreCultureGraduationUseCase {

	private final FindCoreCulturePort findCoreCulturePort;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == CORE_CULTURE;
	}

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<CoreCulture> graduationCoreCultures = findCoreCulturePort.findCoreCulture(user);
		GraduationManager<CoreCulture> coreCultureGraduationManager = new CoreCultureGraduationManager();
		return coreCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCoreCultures, graduationRequirement.getCoreCultureCredit());
	}
}
