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

	private final ParsingAnonymousUseCase parsingAnonymousUseCase;
	private final CheckGraduationRequirementUseCase checkGraduationRequirementUseCase;
	private final QueryParsingTextHistoryPort queryParsingTextHistoryPort;
	private final SaveParsingTextHistoryPort saveParsingTextHistoryPort;
	private final PlatformTransactionManager transactionManager;

	/**
	 * 실패한 성적표를 분석하여 실패 원인을 파악합니다.
	 * 기본 영어 레벨(ENG12)과 한국어 레벨(KOR12)을 사용합니다.
	 *
	 * @param parsingText 분석할 성적표 텍스트
	 * @return 실패 원인 분석 결과 (FailureReason, failureDetails)
	 */
	public FailureAnalysisResult analyzeFailure(String parsingText) {
		return analyzeFailure(parsingText, EnglishLevel.ENG12, KoreanLevel.KOR12);
	}

	/**
	 * 실패한 성적표를 분석하여 실패 원인을 파악합니다.
	 *
	 * @param parsingText 분석할 성적표 텍스트
	 * @param englishLevel 영어 레벨
	 * @param koreanLevel 한국어 레벨
	 * @return 실패 원인 분석 결과 (FailureReason, failureDetails)
	 */
	public FailureAnalysisResult analyzeFailure(
		String parsingText,
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel
	) {
		try {
			// 1단계: 파싱 텍스트 검증
			validateParsingText(parsingText);
		} catch (InvalidPdfException e) {
			log.debug("파싱 텍스트 검증 실패: {}", e.getMessage());
			return new FailureAnalysisResult(
				FailureReason.EMPTY_PARSING_TEXT,
				e.getMessage()
			);
		}

		ParsingInformation parsingInformation;
		try {
			// 2단계: 파싱 정보 추출
			parsingInformation = ParsingInformation.parsing(parsingText);
		} catch (Exception e) {
			log.debug("파싱 정보 추출 실패: {}", e.getMessage(), e);
			return new FailureAnalysisResult(
				FailureReason.PARSING_EXCEPTION,
				"PDF에서 정보를 읽어오는데 실패했습니다: " + e.getMessage()
			);
		}

		try {
			// 3단계: 학생 유형 검증
			checkUnSupportedUser(parsingInformation);
		} catch (IllegalArgumentException e) {
			log.debug("학생 유형 검증 실패: {}", e.getMessage());
			if (e.getMessage().equals(UNSUPPORTED_STUDENT_CATEGORY.toString())) {
				return new FailureAnalysisResult(
					FailureReason.UNSUPPORTED_STUDENT_CATEGORY,
					e.getMessage()
				);
			}
			return new FailureAnalysisResult(
				FailureReason.UNKNOWN_ERROR,
				"학생 유형 검증 중 오류 발생: " + e.getMessage()
			);
		}

		ParsingAnonymousDto parsingAnonymousDto;
		try {
			// 4단계: 익명 사용자 생성 및 과목 정보 검증
			parsingAnonymousDto = parsingAnonymousUseCase.parseAnonymous(
				englishLevel,
				koreanLevel,
				parsingText
			);
		} catch (IllegalArgumentException e) {
			log.debug("과목 정보 검증 실패: {}", e.getMessage());
			if (e.getMessage().equals(NON_EXISTED_LECTURE.toString())) {
				return new FailureAnalysisResult(
					FailureReason.LECTURE_NOT_FOUND,
					e.getMessage()
				);
			}
			if (e.getMessage().equals(UNSUPPORTED_STUDENT_CATEGORY.toString())) {
				return new FailureAnalysisResult(
					FailureReason.UNSUPPORTED_STUDENT_CATEGORY,
					e.getMessage()
				);
			}
			return new FailureAnalysisResult(
				FailureReason.UNKNOWN_ERROR,
				"익명 사용자 생성 중 오류 발생: " + e.getMessage()
			);
		} catch (InvalidPdfException e) {
			log.debug("PDF 검증 실패: {}", e.getMessage());
			if (e.getMessage().contains(INCORRECT_STUDENT_NUMBER.toString())) {
				return new FailureAnalysisResult(
					FailureReason.INCORRECT_STUDENT_NUMBER,
					e.getMessage()
				);
			}
			return new FailureAnalysisResult(
				FailureReason.EMPTY_PARSING_TEXT,
				e.getMessage()
			);
		} catch (PdfParsingException e) {
			log.debug("PDF 파싱 실패: {}", e.getMessage());
			return new FailureAnalysisResult(
				FailureReason.PARSING_EXCEPTION,
				e.getMessage()
			);
		} catch (Exception e) {
			log.debug("익명 사용자 생성 중 예상치 못한 오류: {}", e.getMessage(), e);
			return new FailureAnalysisResult(
				FailureReason.UNKNOWN_ERROR,
				"익명 사용자 생성 중 예상치 못한 오류 발생: " + e.getMessage()
			);
		}

		try {
			// 5단계: 졸업 검사 실행
			checkGraduationRequirementUseCase.checkGraduationRequirement(
				parsingAnonymousDto.getAnonymous(),
				parsingAnonymousDto.getTakenLectureInventory()
			);
		} catch (IllegalArgumentException e) {
			String message = e.getMessage();
			log.debug("졸업 검사 실패 (IllegalArgumentException): {}", message);
			
			if (message != null && message.contains("소속 단과대가 존재하지 않습니다")) {
				return new FailureAnalysisResult(
					FailureReason.MAJOR_NAME_MISMATCH,
					"전공명이 시스템에 등록되지 않았습니다: " + message
				);
			}
			if (message != null && message.contains("해당 과목을 찾을 수 없습니다")) {
				return new FailureAnalysisResult(
					FailureReason.LECTURE_NOT_FOUND,
					message
				);
			}
			// 기타 IllegalArgumentException은 일반 졸업 검사 실패로 분류
			return new FailureAnalysisResult(
				FailureReason.GRADUATION_CHECK_FAILED,
				"졸업 검사 중 오류 발생: " + message
			);
		} catch (NoSuchElementException e) {
			String message = e.getMessage();
			log.debug("졸업 검사 실패 (NoSuchElementException): {}", message);
			
			if (message != null && message.contains("일치하는 졸업 요건이 존재하지 않습니다")) {
				return new FailureAnalysisResult(
					FailureReason.GRADUATION_REQUIREMENT_NOT_FOUND,
					"전공명/입학년도 조합에 맞는 졸업 요건을 찾을 수 없습니다: " + message
				);
			}
			// 기타 NoSuchElementException은 일반 졸업 검사 실패로 분류
			return new FailureAnalysisResult(
				FailureReason.GRADUATION_CHECK_FAILED,
				"졸업 검사 중 오류 발생: " + message
			);
		} catch (Exception e) {
			log.debug("졸업 검사 실패: {}", e.getMessage(), e);
			return new FailureAnalysisResult(
				FailureReason.GRADUATION_CHECK_FAILED,
				"졸업 검사 중 오류 발생: " + e.getMessage()
			);
		}

		// 모든 단계를 통과했다면 성공 (이 경우는 실패 분석이므로 발생하지 않아야 함)
		return new FailureAnalysisResult(
			FailureReason.UNKNOWN_ERROR,
			"모든 검증 단계를 통과했지만 실패로 기록되었습니다."
		);
	}

	/**
	 * 파싱 텍스트가 비어있는지 검증합니다.
	 */
	private void validateParsingText(String parsingText) {
		if (parsingText == null || parsingText.trim().isEmpty()) {
			throw new InvalidPdfException("PDF를 인식하지 못했습니다. 채널톡으로 문의 바랍니다.");
		}
	}

	/**
	 * 지원하지 않는 학생 유형인지 검증합니다.
	 */
	private void checkUnSupportedUser(ParsingInformation parsingInformation) {
		if (parsingInformation.getStudentCategory() == DOUBLE_SUB) {
			throw new IllegalArgumentException(ErrorCode.UNSUPPORTED_STUDENT_CATEGORY.toString());
		}
	}

	private static final int BATCH_SIZE = 100;

	/**
	 * 기존 실패 데이터를 일괄 재분석하여 실패 원인을 업데이트합니다.
	 * failureReason이 null인 기존 실패 데이터를 페이징으로 조회하여 건별로 분석 후 업데이트합니다.
	 *
	 * @return 분석된 실패 데이터 개수
	 */
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

	/**
	 * 실패 원인 분석 결과를 담는 클래스
	 */
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

