package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Slf4j
@UseCase
@Transactional
@RequiredArgsConstructor
class ParsingTextHistoryService implements ParsingTextHistoryUseCase {

	private final SaveParsingTextHistoryPort saveParsingTextHistoryPort;
	private final FindUserUseCase findUserUseCase;
	private final FailureAnalysisService failureAnalysisService;

	@Override
	public void generateSucceedParsingTextHistory(Long userId, String parsingText) {
		User user = findUserUseCase.findUserById(userId);
		saveParsingTextHistoryPort.saveParsingTextHistory(
			ParsingTextHistory.success(user, parsingText));
	}

	@Override
	public void generateFailedParsingTextHistory(Long userId, String parsingText) {
		User user = findUserUseCase.findUserById(userId);
		
		// 실패 원인 분석
		FailureAnalysisService.FailureAnalysisResult analysisResult = 
			failureAnalysisService.analyzeFailure(
				parsingText,
				user.getEnglishLevel(),
				user.getKoreanLevel()
			);
		
		// 실패 원인과 함께 저장
		saveParsingTextHistoryPort.saveParsingTextHistory(
			ParsingTextHistory.fail(
				user,
				parsingText,
				analysisResult.getFailureReason(),
				analysisResult.getFailureDetails()
			)
		);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String generateAnonymousSucceedParsingTextHistory(String parsingText) {
		String trackingCode = UUID.randomUUID().toString();
		saveParsingTextHistoryPort.saveParsingTextHistory(
			ParsingTextHistory.anonymousSuccess(parsingText, trackingCode)
		);
		return trackingCode;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String generateAnonymousFailedParsingTextHistory(
		String parsingText,
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel
	) {
		String trackingCode = UUID.randomUUID().toString();
		FailureAnalysisService.FailureAnalysisResult analysisResult =
			failureAnalysisService.analyzeFailure(parsingText, englishLevel, koreanLevel);
		saveParsingTextHistoryPort.saveParsingTextHistory(
			ParsingTextHistory.anonymousFail(
				parsingText,
				analysisResult.getFailureReason(),
				analysisResult.getFailureDetails(),
				trackingCode
			)
		);
		return trackingCode;
	}
}
