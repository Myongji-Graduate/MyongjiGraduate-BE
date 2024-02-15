package com.plzgraduate.myongjigraduatebe.graduation.application.port;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.CalculateGraduationUseCase;
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
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.submajor.SubMajorManager;
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
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.GraduationRequirementType;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CalculateGraduationService implements CalculateGraduationUseCase {

	private final FindUserUseCase findUserUseCase;

	private final FindTakenLecturePort findTakenLecturePort;
	private final FindCommonCulturePort findCommonCulturePort;
	private final FindCoreCulturePort findCoreCulturePort;
	private final FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	private final FindMajorPort findMajorPort;

	@Override
	public GraduationResponse calculateGraduation(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		GraduationRequirement graduationRequirement = determineGraduationRequirement(user);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(
			findTakenLecturePort.findTakenLectureSetByUser(user));

		ChapelResult chapelResult = generateChapelResult(takenLectureInventory);
		List<DetailGraduationResult> detailGraduationResults = generateDetailGraduationResults(user,
			takenLectureInventory, graduationRequirement);

		GraduationResult graduationResult = generateGraduationResult(chapelResult, detailGraduationResults,
			takenLectureInventory, graduationRequirement);

		return GraduationResponse.of(user, graduationResult);
	}

	private GraduationRequirement determineGraduationRequirement(User user) {
		College userCollage = College.findBelongingCollege(user);
		GraduationRequirementType defaultGraduationRequirement = GraduationRequirementType.determineGraduationRequirement(
			userCollage, user);
		return defaultGraduationRequirement.convertToProfitGraduationRequirement(user);
	}

	private ChapelResult generateChapelResult(TakenLectureInventory takenLectureInventory) {
		ChapelResult chapelResult = ChapelResult.create(takenLectureInventory);
		chapelResult.checkCompleted();
		return chapelResult;
	}

	private List<DetailGraduationResult> generateDetailGraduationResults(User user,
		TakenLectureInventory takenLectureInventory, GraduationRequirement graduationRequirement) {
		List<DetailGraduationResult> detailGraduationResults = new ArrayList<>(List.of(
			generateCommonCultureDetailGraduationResult(
				user, takenLectureInventory, graduationRequirement),
			generateCoreCultureDetailGraduationResult(
				user, takenLectureInventory, graduationRequirement),
			generteBasicAcademicalDetailGraduationResult(
				user, takenLectureInventory, graduationRequirement),
			generateMajorDetailGraduationResult(
				user, takenLectureInventory, graduationRequirement)
		));

		// TODO: Additional Major check - DetailGraduationResult
		if (user.getStudentCategory() == StudentCategory.SUB_MAJOR) {
			detailGraduationResults.add(generateSubMajorDetailGraduationResult(user, takenLectureInventory));
		}

		return detailGraduationResults;
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
		switch (College.findBelongingCollege(user)) {
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
		Set<MajorLecture> graduationMajorLectures = findMajorPort.findMajor(user.getMajor());
		GraduationManager<MajorLecture> majorGraduationManager = new MajorManager();
		return majorGraduationManager.createDetailGraduationResult(user,
			takenLectureInventory, graduationMajorLectures, graduationRequirement.getMajorCredit());
	}

	private DetailGraduationResult generateSubMajorDetailGraduationResult(User user,
		TakenLectureInventory takenLectureInventory) {
		int requireSubMajorCredit = 21;
		Set<MajorLecture> graduationSubMajorLectures = findMajorPort.findMajor(user.getSubMajor());
		GraduationManager<MajorLecture> subMajorManager = new SubMajorManager();
		return subMajorManager.createDetailGraduationResult(user, takenLectureInventory, graduationSubMajorLectures,
			requireSubMajorCredit);
	}

	private GraduationResult generateGraduationResult(ChapelResult chapelResult,
		List<DetailGraduationResult> detailGraduationResults, TakenLectureInventory takenLectureInventory,
		GraduationRequirement graduationRequirement) {
		GraduationResult graduationResult = GraduationResult.create(chapelResult, detailGraduationResults);
		graduationResult.handleLeftTakenLectures(takenLectureInventory, graduationRequirement);
		graduationResult.checkGraduated();
		return graduationResult;
	}
}
