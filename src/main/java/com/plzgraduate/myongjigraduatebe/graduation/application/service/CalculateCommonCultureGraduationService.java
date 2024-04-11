package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateCommonCultureGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture.CommonCultureGraduationManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateCommonCultureGraduationService implements CalculateCommonCultureGraduationUseCase {

	private final FindCommonCulturePort findCommonCulturePort;

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		int totalCredit) {
		Set<CommonCulture> graduationCommonCultures = findCommonCulturePort.findCommonCulture(user);
		GraduationManager<CommonCulture> commonCultureGraduationManager = new CommonCultureGraduationManager();
		return commonCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCommonCultures, totalCredit);
	}

}
