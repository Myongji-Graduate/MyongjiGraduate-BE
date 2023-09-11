package com.plzgraduate.myongjigraduatebe.graduation.application.port;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.*;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.CheckGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.out.LoadGraduationRequirementPort;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.GraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.BusinessBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.DefaultBasicAcademicalManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture.SocialScienceBasicAcademicManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.commonculture.CommonCultureGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.coreculture.CoreCultureGraduationManager;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.MajorManager;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.LoadMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Major;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.LoadTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CheckGraduationService implements CheckGraduationUseCase {

	private final LoadGraduationRequirementPort loadGraduationRequirementPort;
	private final LoadTakenLecturePort loadTakenLecturePort;
	private final LoadCommonCulturePort loadCommonCulturePort;
	private final LoadCoreCulturePort loadCoreCulturePort;
	private final LoadBasicAcademicalCulturePort loadBasicAcademicalCulturePort;
	private final LoadMajorPort loadMajorPort;

	@Override
	public GraduationResponse checkGraduation(User user) {
		GraduationRequirement graduationRequirement = loadGraduationRequirementPort.loadGraduationRequirement(user);
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(
			loadTakenLecturePort.loadTakenLectures(user));

		ChapelResult chapelResult = generateChapelResult(takenLectureInventory);

		List<DetailGraduationResult> detailGraduationResults = generateDetailGraduationResults(user,
			takenLectureInventory, graduationRequirement);

		GraduationResult graduationResult = generateGraduationResult(chapelResult, detailGraduationResults,
			takenLectureInventory, graduationRequirement);

		return GraduationResponse.of(user, graduationResult);
	}

	private ChapelResult generateChapelResult(TakenLectureInventory takenLectureInventory) {
		ChapelResult chapelResult = ChapelResult.create(takenLectureInventory);
		chapelResult.checkCompleted();
		return chapelResult;
	}

	private List<DetailGraduationResult> generateDetailGraduationResults(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		DetailGraduationResult commonCultureGraduationResult = generateCommonCultureDetailGraduationResult(
			user, takenLectureInventory, graduationRequirement);

		DetailGraduationResult coreCultureDetailGraduationResult = generateCoreCultureDetailGraduationResult(
			user, takenLectureInventory, graduationRequirement);

		DetailGraduationResult basicAcademicalDetailGraduationResult = generteBasicAcademicalDetailGraduationResult(
			user, takenLectureInventory, graduationRequirement);

		DetailGraduationResult majorDetailGraduationResult = generateMajorDetailGraduationResult(
			user, takenLectureInventory, graduationRequirement);

		return List.of(commonCultureGraduationResult, coreCultureDetailGraduationResult,
			basicAcademicalDetailGraduationResult, majorDetailGraduationResult);
	}

	private DetailGraduationResult generateCommonCultureDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<CommonCulture> graduationCommonCultures = loadCommonCulturePort.loadCommonCulture(user);
		GraduationManager<CommonCulture> commonCultureGraduationManager = new CommonCultureGraduationManager();
		return commonCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCommonCultures, graduationRequirement.getCommonCultureCredit());
	}

	private DetailGraduationResult generateCoreCultureDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<CoreCulture> graduationCoreCultures = loadCoreCulturePort.loadCoreCulture(user);
		GraduationManager<CoreCulture> coreCultureGraduationManager = new CoreCultureGraduationManager();
		return coreCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCoreCultures, graduationRequirement.getCoreCultureCredit());
	}

	private DetailGraduationResult generteBasicAcademicalDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<BasicAcademicalCulture> graduationBasicAcademicalCultures = loadBasicAcademicalCulturePort.loadBasicAcademicalCulture(
			user);
		GraduationManager<BasicAcademicalCulture> basicAcademicalCultureGraduationManager = determineBasicAcademicalCultureGraduationManager(
			user);
		return basicAcademicalCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationBasicAcademicalCultures,
			graduationRequirement.getBasicAcademicalCredit());
	}

	private static GraduationManager<BasicAcademicalCulture> determineBasicAcademicalCultureGraduationManager(
		User user) {
		GraduationManager<BasicAcademicalCulture> basicAcademicalCultureGraduationManager;
		switch (findBelongingCollege(user.getMajor())) {
			case BUSINESS:
				basicAcademicalCultureGraduationManager = new BusinessBasicAcademicalManager();
				break;
			case SOCIAL_SCIENCE:
				basicAcademicalCultureGraduationManager = new SocialScienceBasicAcademicManager();
				break;
			default:
				basicAcademicalCultureGraduationManager = new DefaultBasicAcademicalManager();
				break;
		}
		return basicAcademicalCultureGraduationManager;
	}

	private DetailGraduationResult generateMajorDetailGraduationResult(User user, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		Set<Major> graduationMajors = loadMajorPort.loadMajor(user);
		GraduationManager<Major> majorGraduationManager = new MajorManager();
		return majorGraduationManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationMajors, graduationRequirement.getMajorCredit());
	}

	private GraduationResult generateGraduationResult(ChapelResult chapelResult,
		List<DetailGraduationResult> detailGraduationResults,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		GraduationResult graduationResult = GraduationResult.create(chapelResult, detailGraduationResults);
		graduationResult.handleLeftTakenLectures(takenLectureInventory, graduationRequirement);
		graduationResult.checkGraduated();
		return graduationResult;
	}
}
