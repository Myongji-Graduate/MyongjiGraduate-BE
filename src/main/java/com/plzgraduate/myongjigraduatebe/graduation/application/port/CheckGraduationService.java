package com.plzgraduate.myongjigraduatebe.graduation.application.port;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.*;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.CheckGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.out.FindGraduationRequirementPort;
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
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CheckGraduationService implements CheckGraduationUseCase {

	private final FindGraduationRequirementPort findGraduationRequirementPort;
	private final FindTakenLecturePort findTakenLecturePort;
	private final FindCommonCulturePort findCommonCulturePort;
	private final FindCoreCulturePort findCoreCulturePort;
	private final FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	private final FindMajorPort findMajorPort;

	@Override
	public GraduationResponse checkGraduation(User user) {
		GraduationRequirement graduationRequirement = findGraduationRequirementPort.findGraduationRequirement(user);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(
			findTakenLecturePort.findTakenLectureSetByUser(user));

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
		Set<CommonCulture> graduationCommonCultures = findCommonCulturePort.findCommonCulture(user);
		GraduationManager<CommonCulture> commonCultureGraduationManager = new CommonCultureGraduationManager();
		return commonCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCommonCultures, graduationRequirement.getCommonCultureCredit());
	}

	private DetailGraduationResult generateCoreCultureDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<CoreCulture> graduationCoreCultures = findCoreCulturePort.findCoreCulture(user);
		GraduationManager<CoreCulture> coreCultureGraduationManager = new CoreCultureGraduationManager();
		return coreCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationCoreCultures, graduationRequirement.getCoreCultureCredit());
	}

	private DetailGraduationResult generteBasicAcademicalDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<BasicAcademicalCultureLecture> graduationBasicAcademicalCultureLectures = findBasicAcademicalCulturePort.findBasicAcademicalCulture(
			user);
		GraduationManager<BasicAcademicalCultureLecture> basicAcademicalCultureGraduationManager = determineBasicAcademicalCultureGraduationManager(
			user);
		return basicAcademicalCultureGraduationManager.createDetailGraduationResult(
			user, takenLectureInventory, graduationBasicAcademicalCultureLectures,
			graduationRequirement.getBasicAcademicalCredit());
	}

	private GraduationManager<BasicAcademicalCultureLecture> determineBasicAcademicalCultureGraduationManager(
		User user) {
		GraduationManager<BasicAcademicalCultureLecture> basicAcademicalCultureGraduationManager;
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

	private DetailGraduationResult generateMajorDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		Set<MajorLecture> graduationMajorLectures = findMajorPort.findMajor(user);
		GraduationManager<MajorLecture> majorGraduationManager = new MajorManager();
		return majorGraduationManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationMajorLectures, graduationRequirement.getMajorCredit());
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
