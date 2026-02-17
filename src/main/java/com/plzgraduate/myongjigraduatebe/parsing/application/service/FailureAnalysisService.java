package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INCORRECT_STUDENT_NUMBER;
import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.NON_EXISTED_LECTURE;
import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.UNSUPPORTED_STUDENT_CATEGORY;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.DOUBLE_SUB;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.QueryParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingAnonymousUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class FailureAnalysisService {

	private static final String GRADUATION_CHECK_ERROR_PREFIX = "졸업 검사 중 오류 발생: ";
	private static final int BATCH_SIZE = 100;

	private final ParsingAnonymousUseCase parsingAnonymousUseCase;
	private final CheckGraduationRequirementUseCase checkGraduationRequirementUseCase;
	private final QueryParsingTextHistoryPort queryParsingTextHistoryPort;
	private final SaveParsingTextHistoryPort saveParsingTextHistoryPort;
	private final PlatformTransactionManager transactionManager;

	public FailureAnalysisResult analyzeFailure(String parsingText) {
		return analyzeFailure(parsingText, EnglishLevel.ENG12, KoreanLevel.KOR12);
	}

	public FailureAnalysisResult analyzeFailure(
		String parsingText,
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel
	) {
		FailureAnalysisResult result;

		result = validateAndParseText(parsingText);
		if (result != null) {
			return result;
		}

		ParsingInformation parsingInformation;
		try {
			parsingInformation = ParsingInformation.parsing(parsingText);
		} catch (Exception e) {
			log.debug("파싱 정보 추출 실패: {}", e.getMessage(), e);
			return new FailureAnalysisResult(
				FailureReason.PARSING_EXCEPTION,
				"PDF에서 정보를 읽어오는데 실패했습니다: " + e.getMessage()
			);
		}

		result = checkStudentCategory(parsingInformation);
		if (result != null) {
			return result;
		}

		ParsingAnonymousDto parsingAnonymousDto;
		try {
			parsingAnonymousDto = parsingAnonymousUseCase.parseAnonymous(
				englishLevel, koreanLevel, parsingText
			);
		} catch (Exception e) {
			return classifyAnonymousParsingException(e);
		}

		return checkGraduation(parsingAnonymousDto);
	}

	private FailureAnalysisResult validateAndParseText(String parsingText) {
		try {
			validateParsingText(parsingText);
			return null;
		} catch (InvalidPdfException e) {
			log.debug("파싱 텍스트 검증 실패: {}", e.getMessage());
			return new FailureAnalysisResult(FailureReason.EMPTY_PARSING_TEXT, e.getMessage());
		}
	}

	private FailureAnalysisResult checkStudentCategory(ParsingInformation parsingInformation) {
		try {
			checkUnSupportedUser(parsingInformation);
			return null;
		} catch (IllegalArgumentException e) {
			log.debug("학생 유형 검증 실패: {}", e.getMessage());
			if (e.getMessage().equals(UNSUPPORTED_STUDENT_CATEGORY.toString())) {
				return new FailureAnalysisResult(FailureReason.UNSUPPORTED_STUDENT_CATEGORY, e.getMessage());
			}
			return new FailureAnalysisResult(FailureReason.UNKNOWN_ERROR, "학생 유형 검증 중 오류 발생: " + e.getMessage());
		}
	}

	private FailureAnalysisResult classifyAnonymousParsingException(Exception e) {
		if (e instanceof IllegalArgumentException) {
			log.debug("과목 정보 검증 실패: {}", e.getMessage());
			if (e.getMessage().equals(NON_EXISTED_LECTURE.toString())) {
				return new FailureAnalysisResult(FailureReason.LECTURE_NOT_FOUND, e.getMessage());
			}
			if (e.getMessage().equals(UNSUPPORTED_STUDENT_CATEGORY.toString())) {
				return new FailureAnalysisResult(FailureReason.UNSUPPORTED_STUDENT_CATEGORY, e.getMessage());
			}
			return new FailureAnalysisResult(FailureReason.UNKNOWN_ERROR, "익명 사용자 생성 중 오류 발생: " + e.getMessage());
		}
		if (e instanceof InvalidPdfException) {
			log.debug("PDF 검증 실패: {}", e.getMessage());
			if (e.getMessage().contains(INCORRECT_STUDENT_NUMBER.toString())) {
				return new FailureAnalysisResult(FailureReason.INCORRECT_STUDENT_NUMBER, e.getMessage());
			}
			return new FailureAnalysisResult(FailureReason.EMPTY_PARSING_TEXT, e.getMessage());
		}
		if (e instanceof PdfParsingException) {
			log.debug("PDF 파싱 실패: {}", e.getMessage());
			return new FailureAnalysisResult(FailureReason.PARSING_EXCEPTION, e.getMessage());
		}
		log.debug("익명 사용자 생성 중 예상치 못한 오류: {}", e.getMessage(), e);
		return new FailureAnalysisResult(FailureReason.UNKNOWN_ERROR, "익명 사용자 생성 중 예상치 못한 오류 발생: " + e.getMessage());
	}

	private FailureAnalysisResult checkGraduation(ParsingAnonymousDto parsingAnonymousDto) {
		try {
			checkGraduationRequirementUseCase.checkGraduationRequirement(
				parsingAnonymousDto.getAnonymous(),
				parsingAnonymousDto.getTakenLectureInventory()
			);
		} catch (IllegalArgumentException e) {
			return classifyGraduationIllegalArgument(e);
		} catch (NoSuchElementException e) {
			return classifyGraduationNoSuchElement(e);
		} catch (Exception e) {
			log.debug("졸업 검사 실패: {}", e.getMessage(), e);
			return new FailureAnalysisResult(FailureReason.GRADUATION_CHECK_FAILED, GRADUATION_CHECK_ERROR_PREFIX + e.getMessage());
		}

		return new FailureAnalysisResult(
			FailureReason.UNKNOWN_ERROR,
			"모든 검증 단계를 통과했지만 실패로 기록되었습니다."
		);
	}

	private FailureAnalysisResult classifyGraduationIllegalArgument(IllegalArgumentException e) {
		String message = e.getMessage();
		log.debug("졸업 검사 실패 (IllegalArgumentException): {}", message);

		if (message != null && message.contains("소속 단과대가 존재하지 않습니다")) {
			return new FailureAnalysisResult(FailureReason.MAJOR_NAME_MISMATCH, "전공명이 시스템에 등록되지 않았습니다: " + message);
		}
		if (message != null && message.contains("해당 과목을 찾을 수 없습니다")) {
			return new FailureAnalysisResult(FailureReason.LECTURE_NOT_FOUND, message);
		}
		return new FailureAnalysisResult(FailureReason.GRADUATION_CHECK_FAILED, GRADUATION_CHECK_ERROR_PREFIX + message);
	}

	private FailureAnalysisResult classifyGraduationNoSuchElement(NoSuchElementException e) {
		String message = e.getMessage();
		log.debug("졸업 검사 실패 (NoSuchElementException): {}", message);

		if (message != null && message.contains("일치하는 졸업 요건이 존재하지 않습니다")) {
			return new FailureAnalysisResult(
				FailureReason.GRADUATION_REQUIREMENT_NOT_FOUND,
				"전공명/입학년도 조합에 맞는 졸업 요건을 찾을 수 없습니다: " + message
			);
		}
		return new FailureAnalysisResult(FailureReason.GRADUATION_CHECK_FAILED, GRADUATION_CHECK_ERROR_PREFIX + message);
	}

	private void validateParsingText(String parsingText) {
		if (parsingText == null || parsingText.trim().isEmpty()) {
			throw new InvalidPdfException("PDF를 인식하지 못했습니다. 채널톡으로 문의 바랍니다.");
		}
	}

	private void checkUnSupportedUser(ParsingInformation parsingInformation) {
		if (parsingInformation.getStudentCategory() == DOUBLE_SUB) {
			throw new IllegalArgumentException(ErrorCode.UNSUPPORTED_STUDENT_CATEGORY.toString());
		}
	}

	public int analyzeExistingFailures() {
		log.info("기존 실패 데이터 재분석 시작");

		int analyzedCount = 0;
		List<ParsingTextHistory> batch;

		while (!(batch = queryParsingTextHistoryPort
				.findByParsingResultAndFailureReasonIsNull(PageRequest.of(0, BATCH_SIZE))).isEmpty()) {
			for (ParsingTextHistory history : batch) {
				analyzedCount += analyzeAndSaveSingle(history);
			}
			log.info("현재까지 {}개 분석 완료", analyzedCount);
		}

		log.info("기존 실패 데이터 재분석 완료. 총 {}개 분석됨", analyzedCount);
		return analyzedCount;
	}

	private int analyzeAndSaveSingle(ParsingTextHistory history) {
		try {
			TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
			Integer result = txTemplate.execute(status -> {
				FailureAnalysisResult analysisResult = analyzeFailure(
					history.getParsingText(),
					history.getUser().getEnglishLevel(),
					history.getUser().getKoreanLevel()
				);

				ParsingTextHistory updatedHistory = ParsingTextHistory.builder()
					.id(history.getId())
					.user(history.getUser())
					.parsingText(history.getParsingText())
					.parsingResult(history.getParsingResult())
					.failureReason(analysisResult.getFailureReason())
					.failureDetails(analysisResult.getFailureDetails())
					.build();

				saveParsingTextHistoryPort.saveParsingTextHistory(updatedHistory);

				log.debug("실패 데이터 ID: {} 분석 완료. 원인: {}",
					history.getId(), analysisResult.getFailureReason());
				return 1;
			});
			return result != null ? result : 0;
		} catch (Exception e) {
			log.error("실패 데이터 ID: {} 분석 중 오류 발생: {}",
				history.getId(), e.getMessage(), e);
			return 0;
		}
	}

	@Getter
	public static class FailureAnalysisResult {
		private final FailureReason failureReason;
		private final String failureDetails;

		public FailureAnalysisResult(FailureReason failureReason, String failureDetails) {
			this.failureReason = failureReason;
			this.failureDetails = failureDetails;
		}
	}
}