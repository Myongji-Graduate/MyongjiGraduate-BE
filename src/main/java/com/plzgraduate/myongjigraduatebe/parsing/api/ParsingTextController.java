package com.plzgraduate.myongjigraduatebe.parsing.api;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.plzgraduate.myongjigraduatebe.core.exception.UnAuthorizedException;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.api.dto.request.ParsingTextRequest;
import com.plzgraduate.myongjigraduatebe.parsing.api.dto.response.AnalyzeExistingFailuresResponse;
import com.plzgraduate.myongjigraduatebe.parsing.application.service.FailureAnalysisService;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;

@WebAdapter
@RequestMapping("/api/v1/parsing-text")
@RequiredArgsConstructor
public class ParsingTextController implements ParsingTextApiPresentation {

	private final ParsingTextUseCase parsingTextUseCase;
	private final ParsingTextHistoryUseCase parsingTextHistoryUseCase;
	private final TakenLectureCacheEvict takenLectureCacheEvict;
	private final FailureAnalysisService failureAnalysisService;

	@Value("${admin.api-key}")
	private String adminApiKey;

	@PostMapping
	public void enrollParsingText(
		@LoginUser Long userId,
		@Valid @RequestBody ParsingTextRequest parsingTextRequest
	) {
		takenLectureCacheEvict.evictTakenLecturesCache(userId);
		try {
			parsingTextUseCase.enrollParsingText(userId, parsingTextRequest.getParsingText());
			parsingTextHistoryUseCase.generateSucceedParsingTextHistory(
				userId,
				parsingTextRequest.getParsingText()
			);
		} catch (Exception e) {
			parsingTextHistoryUseCase.generateFailedParsingTextHistory(
				userId,
				parsingTextRequest.getParsingText()
			);
			throw e;
		}
	}

	/**
	 * 기존 실패 데이터를 재분석하여 실패 원인을 업데이트합니다.
	 * failureReason이 null인 기존 실패 데이터를 일괄 분석합니다.
	 *
	 * @return 분석된 실패 데이터 개수
	 */
	@PostMapping("/analyze-existing-failures")
	public AnalyzeExistingFailuresResponse analyzeExistingFailures(
		@RequestHeader(value = "X-Admin-Key", required = false) String apiKey
	) {
		validateAdminApiKey(apiKey);
		int analyzedCount = failureAnalysisService.analyzeExistingFailures();
		return AnalyzeExistingFailuresResponse.of(analyzedCount);
	}

	private void validateAdminApiKey(String apiKey) {
		if (apiKey == null || !MessageDigest.isEqual(
				apiKey.getBytes(StandardCharsets.UTF_8),
				adminApiKey.getBytes(StandardCharsets.UTF_8))) {
			throw new UnAuthorizedException("유효하지 않은 관리자 API 키입니다.");
		}
	}
}
