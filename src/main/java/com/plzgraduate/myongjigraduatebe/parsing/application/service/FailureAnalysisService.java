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
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingAnonymousUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
public class FailureAnalysisService {

	private final ParsingAnonymousUseCase parsingAnonymousUseCase;
	private final CheckGraduationRequirementUseCase checkGraduationRequirementUseCase;

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

	/**
	 * 실패 원인 분석 결과를 담는 클래스
	 */
	public static class FailureAnalysisResult {
		private final FailureReason failureReason;
		private final String failureDetails;

		public FailureAnalysisResult(FailureReason failureReason, String failureDetails) {
			this.failureReason = failureReason;
			this.failureDetails = failureDetails;
		}

		public FailureReason getFailureReason() {
			return failureReason;
		}

		public String getFailureDetails() {
			return failureDetails;
		}
	}
}

