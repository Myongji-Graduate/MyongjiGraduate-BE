package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture.CommonGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateCommonCultureGraduationService implements CalculateDetailGraduationUseCase {

	private final FindCommonCulturePort findCommonCulturePort;
	private final CommonGraduationManager commonCultureGraduationManager;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == COMMON_CULTURE;
	}

	@Override
	public DetailGraduationResult calculateSingleDetailGraduation(User user,
		GraduationCategory graduationCategory,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<CommonCulture> graduationCommonCultures = findCommonCulturePort.findCommonCulture(user);

		return commonCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCommonCultures,
			graduationRequirement.getCommonCultureCredit());
	}
}
