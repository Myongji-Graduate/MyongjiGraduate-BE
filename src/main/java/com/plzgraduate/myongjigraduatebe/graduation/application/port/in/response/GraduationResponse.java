package com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationResponse {

	private final BasicInfoResponse basicInfo;
	private final ChapelResultResponse chapelResult;
	private final DetailGraduationResultResponse commonCulture;
	private final DetailGraduationResultResponse coreCulture;
	private final DetailGraduationResultResponse basicAcademicalCulture;
	private final DetailGraduationResultResponse major;
	private final RestResultResponse normalCulture;
	private final RestResultResponse freeElective;
	private final boolean graduated;

	@Builder
	private GraduationResponse(BasicInfoResponse basicInfo, ChapelResultResponse chapelResult,
		DetailGraduationResultResponse commonCulture, DetailGraduationResultResponse coreCulture,
		DetailGraduationResultResponse basicAcademicalCulture, DetailGraduationResultResponse major,
		RestResultResponse normalCulture, RestResultResponse freeElective, boolean graduated) {
		this.basicInfo = basicInfo;
		this.chapelResult = chapelResult;
		this.commonCulture = commonCulture;
		this.coreCulture = coreCulture;
		this.basicAcademicalCulture = basicAcademicalCulture;
		this.major = major;
		this.normalCulture = normalCulture;
		this.freeElective = freeElective;
		this.graduated = graduated;
	}

	public static GraduationResponse of(User user, GraduationResult graduationResult) {
		return GraduationResponse.builder()
			.basicInfo(BasicInfoResponse.of(user, graduationResult))
			.chapelResult(ChapelResultResponse.from(graduationResult.getChapelResult()))
			.commonCulture(findDetailGraduationResultResponse(graduationResult, COMMON_CULTURE))
			.coreCulture(findDetailGraduationResultResponse(graduationResult, CORE_CULTURE))
			.basicAcademicalCulture(findDetailGraduationResultResponse(graduationResult, BASIC_ACADEMICAL_CULTURE))
			.major(findDetailGraduationResultResponse(graduationResult, MAJOR))
			.normalCulture(
				RestResultResponse.fromNormalCultureResult(graduationResult.getNormalCultureGraduationResult()))
			.freeElective(RestResultResponse.fromFreeElectiveResult(graduationResult.getFreeElectiveGraduationResult()))
			.graduated(graduationResult.isGraduated())
			.build();
	}

	private static DetailGraduationResultResponse findDetailGraduationResultResponse(GraduationResult graduationResult,
		GraduationCategory graduationCategory) {
		return graduationResult.getDetailGraduationResults().stream()
			.filter(detailGraduationResult -> detailGraduationResult.getCategoryName()
				.equals(graduationCategory.getName()))
			.map(DetailGraduationResultResponse::from)
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);
	}
}
