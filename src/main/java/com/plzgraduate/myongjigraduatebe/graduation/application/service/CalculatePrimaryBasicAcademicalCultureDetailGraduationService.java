package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculatePrimaryBasicAcademicalCultureDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BusinessBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.DefaultBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.SocialScienceBasicAcademicManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculatePrimaryBasicAcademicalCultureDetailGraduationService
	implements CalculatePrimaryBasicAcademicalCultureDetailGraduationUseCase {

	private final FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;

	@Override
	public boolean supports(GraduationCategory graduationCategory) {
		return graduationCategory == PRIMARY_BASIC_ACADEMICAL_CULTURE;
	}

	@Override
	public DetailGraduationResult calculateDetailGraduation(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<BasicAcademicalCultureLecture> graduationBasicAcademicalCultureLectures = findBasicAcademicalCulturePort.findBasicAcademicalCulture(
			user);
		GraduationManager<BasicAcademicalCultureLecture> basicAcademicalCultureGraduationManager = determineBasicAcademicalCultureGraduationManager(
			user);
		DetailGraduationResult detailGraduationResult = basicAcademicalCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationBasicAcademicalCultureLectures,
			graduationRequirement.getPrimaryBasicAcademicalCultureCredit());
		detailGraduationResult.assignGraduationCategory(PRIMARY_BASIC_ACADEMICAL_CULTURE);
		return detailGraduationResult;
	}

	private GraduationManager<BasicAcademicalCultureLecture> determineBasicAcademicalCultureGraduationManager(
		User user) {
		List<BasicAcademicalManager> basicAcademicalManagers = List.of(
			new BusinessBasicAcademicalManager(),
			new SocialScienceBasicAcademicManager());

		return basicAcademicalManagers.stream()
			.filter(basicAcademicalManager -> basicAcademicalManager.isSatisfied(user.getPrimaryMajor()))
			.findFirst()
			.orElse(new DefaultBasicAcademicalManager());
	}
}
