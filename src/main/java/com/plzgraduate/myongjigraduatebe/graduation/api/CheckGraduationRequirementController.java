package com.plzgraduate.myongjigraduatebe.graduation.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.core.exception.AnonymousGraduationCheckException;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.request.CheckGraduationRequirementRequest;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.CheckGraduationRequirementResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingAnonymousUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response.UserInformationResponse;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/api/v1/graduations/check")
@RequiredArgsConstructor
@Slf4j
public class CheckGraduationRequirementController implements CheckGraduationRequirementApiPresentation {

	private final ParsingAnonymousUseCase parsingAnonymousUseCase;
	private final CheckGraduationRequirementUseCase checkGraduationRequirementUseCase;
	private final ParsingTextHistoryUseCase parsingTextHistoryUseCase;

	@PostMapping
	public CheckGraduationRequirementResponse checkGraduationRequirement(
		@Valid @RequestBody CheckGraduationRequirementRequest checkGraduationRequirementRequest
	) {
		EnglishLevel englishLevel = EnglishLevel.ENG12;
		KoreanLevel koreanLevel = KoreanLevel.KOR12;
		String parsingText = checkGraduationRequirementRequest.getParsingText();
		try {
			englishLevel = checkGraduationRequirementRequest.getEngLv();
			koreanLevel = checkGraduationRequirementRequest.getKorLv();
			ParsingAnonymousDto parsingAnonymousDto = parsingAnonymousUseCase.parseAnonymous(
				englishLevel,
				koreanLevel,
				parsingText
			);
			User anonymous = parsingAnonymousDto.getAnonymous();
			GraduationResult graduationResult = checkGraduationRequirementUseCase.checkGraduationRequirement(
				anonymous,
				parsingAnonymousDto.getTakenLectureInventory()
			);
			String trackingCode = saveSuccessHistory(parsingText);
			return new CheckGraduationRequirementResponse(
				UserInformationResponse.of(anonymous, graduationResult),
				graduationResult,
				trackingCode
			);
		} catch (Exception exception) {
			String trackingCode = saveFailureHistory(
				parsingText,
				englishLevel,
				koreanLevel
			);
			throw new AnonymousGraduationCheckException(trackingCode, exception);
		}
	}

	private String saveSuccessHistory(String parsingText) {
		try {
			return parsingTextHistoryUseCase.generateAnonymousSucceedParsingTextHistory(parsingText);
		} catch (Exception historyException) {
			log.error("Failed to save anonymous success parsing history", historyException);
			return null;
		}
	}

	private String saveFailureHistory(
		String parsingText,
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel
	) {
		try {
			return parsingTextHistoryUseCase.generateAnonymousFailedParsingTextHistory(
				parsingText,
				englishLevel,
				koreanLevel
			);
		} catch (Exception historyException) {
			log.error("Failed to save anonymous failure parsing history", historyException);
			return null;
		}
	}
}
