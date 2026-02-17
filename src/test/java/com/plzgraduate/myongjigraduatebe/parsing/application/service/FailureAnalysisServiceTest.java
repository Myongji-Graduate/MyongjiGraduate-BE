package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INCORRECT_STUDENT_NUMBER;
import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.NON_EXISTED_LECTURE;
import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.UNSUPPORTED_STUDENT_CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.plzgraduate.myongjigraduatebe.core.exception.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.core.exception.PdfParsingException;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.QueryParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingAnonymousUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
class FailureAnalysisServiceTest {

	@Mock
	private ParsingAnonymousUseCase parsingAnonymousUseCase;
	@Mock
	private CheckGraduationRequirementUseCase checkGraduationRequirementUseCase;
	@Mock
	private QueryParsingTextHistoryPort queryParsingTextHistoryPort;
	@Mock
	private SaveParsingTextHistoryPort saveParsingTextHistoryPort;
	@SuppressWarnings("unused")
	@Mock
	private PlatformTransactionManager transactionManager;

	@InjectMocks
	private FailureAnalysisService failureAnalysisService;

	private static final String VALID_PARSING_TEXT = "출력일자 :  2023/09/01|1/1"
		+ "|경영대학 경영학과, 이아현(60191000), 현학적 - 재학, 이수 - 6, 입학 - 신입학(2019/03/04)"
		+ "|토익 - 625, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2021/07/20)"
		+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
		+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
		+ "|공통교양 15, 핵심교양 12, 학문기초교양 6, 일반교양 22, 전공 50, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 0"
		+ "|총 취득학점 - 105, 총점 - 368.5, 평균평점 - 4.14"
		+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|";

	@DisplayName("파싱 텍스트가 null이면 EMPTY_PARSING_TEXT를 반환한다")
	@Test
	void analyzeFailure_nullParsingText() {
		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(null);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.EMPTY_PARSING_TEXT);
	}

	@DisplayName("파싱 텍스트가 빈 문자열이면 EMPTY_PARSING_TEXT를 반환한다")
	@Test
	void analyzeFailure_emptyParsingText() {
		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure("   ");

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.EMPTY_PARSING_TEXT);
	}

	@DisplayName("파싱 정보 추출에 실패하면 PARSING_EXCEPTION을 반환한다")
	@Test
	void analyzeFailure_parsingException() {
		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure("잘못된 텍스트");

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.PARSING_EXCEPTION);
		assertThat(result.getFailureDetails()).contains("PDF에서 정보를 읽어오는데 실패했습니다");
	}

	@DisplayName("익명 사용자 생성 중 과목 미존재 오류가 발생하면 LECTURE_NOT_FOUND를 반환한다")
	@Test
	void analyzeFailure_lectureNotFound() {
		//given
		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willThrow(new IllegalArgumentException(NON_EXISTED_LECTURE.toString()));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.LECTURE_NOT_FOUND);
	}

	@DisplayName("익명 사용자 생성 중 미지원 학생 유형 오류가 발생하면 UNSUPPORTED_STUDENT_CATEGORY를 반환한다")
	@Test
	void analyzeFailure_unsupportedStudentCategoryInAnonymousParsing() {
		//given
		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willThrow(new IllegalArgumentException(UNSUPPORTED_STUDENT_CATEGORY.toString()));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.UNSUPPORTED_STUDENT_CATEGORY);
	}

	@DisplayName("익명 사용자 생성 중 알 수 없는 IllegalArgumentException이 발생하면 UNKNOWN_ERROR를 반환한다")
	@Test
	void analyzeFailure_unknownIllegalArgumentInAnonymousParsing() {
		//given
		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willThrow(new IllegalArgumentException("알 수 없는 오류"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.UNKNOWN_ERROR);
		assertThat(result.getFailureDetails()).contains("익명 사용자 생성 중 오류 발생");
	}

	@DisplayName("익명 사용자 생성 중 학번 불일치 오류가 발생하면 INCORRECT_STUDENT_NUMBER를 반환한다")
	@Test
	void analyzeFailure_incorrectStudentNumber() {
		//given
		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willThrow(new InvalidPdfException(INCORRECT_STUDENT_NUMBER.toString()));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.INCORRECT_STUDENT_NUMBER);
	}

	@DisplayName("익명 사용자 생성 중 InvalidPdfException이 발생하면 EMPTY_PARSING_TEXT를 반환한다")
	@Test
	void analyzeFailure_invalidPdfInAnonymousParsing() {
		//given
		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willThrow(new InvalidPdfException("PDF 형식 오류"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.EMPTY_PARSING_TEXT);
	}

	@DisplayName("익명 사용자 생성 중 PdfParsingException이 발생하면 PARSING_EXCEPTION을 반환한다")
	@Test
	void analyzeFailure_pdfParsingExceptionInAnonymousParsing() {
		//given
		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willThrow(new PdfParsingException("PDF 파싱 실패"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.PARSING_EXCEPTION);
	}

	@DisplayName("익명 사용자 생성 중 예상치 못한 오류가 발생하면 UNKNOWN_ERROR를 반환한다")
	@Test
	void analyzeFailure_unexpectedExceptionInAnonymousParsing() {
		//given
		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willThrow(new RuntimeException("예상치 못한 오류"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.UNKNOWN_ERROR);
		assertThat(result.getFailureDetails()).contains("예상치 못한 오류 발생");
	}

	@DisplayName("졸업 검사 실패 - 전공명 불일치 (IllegalArgumentException: 소속 단과대가 존재하지 않습니다)")
	@Test
	void analyzeFailure_majorNameMismatch() {
		//given
		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.MAJOR_NAME_MISMATCH);
		assertThat(result.getFailureDetails()).contains("전공명이 시스템에 등록되지 않았습니다");
	}

	@DisplayName("졸업 검사 실패 - 과목 미존재 (IllegalArgumentException: 해당 과목을 찾을 수 없습니다)")
	@Test
	void analyzeFailure_lectureNotFoundInGraduation() {
		//given
		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new IllegalArgumentException("해당 과목을 찾을 수 없습니다"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.LECTURE_NOT_FOUND);
	}

	@DisplayName("졸업 검사 실패 - 졸업 요건 없음 (NoSuchElementException: 일치하는 졸업 요건이 존재하지 않습니다)")
	@Test
	void analyzeFailure_graduationRequirementNotFound() {
		//given
		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new NoSuchElementException("일치하는 졸업 요건이 존재하지 않습니다."));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.GRADUATION_REQUIREMENT_NOT_FOUND);
		assertThat(result.getFailureDetails()).contains("전공명/입학년도 조합에 맞는 졸업 요건을 찾을 수 없습니다");
	}

	@DisplayName("졸업 검사 실패 - 기타 NoSuchElementException은 GRADUATION_CHECK_FAILED를 반환한다")
	@Test
	void analyzeFailure_otherNoSuchElementException() {
		//given
		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new NoSuchElementException("기타 오류"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.GRADUATION_CHECK_FAILED);
		assertThat(result.getFailureDetails()).contains("졸업 검사 중 오류 발생");
	}

	@DisplayName("졸업 검사 실패 - 일반 오류 (기타 Exception)")
	@Test
	void analyzeFailure_generalGraduationCheckFailed() {
		//given
		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new RuntimeException("예상치 못한 오류"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.GRADUATION_CHECK_FAILED);
		assertThat(result.getFailureDetails()).contains("졸업 검사 중 오류 발생");
	}

	@DisplayName("모든 검증 단계를 통과하면 UNKNOWN_ERROR를 반환한다")
	@Test
	void analyzeFailure_allStepsPassed() {
		//given
		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(eq(EnglishLevel.ENG12), eq(KoreanLevel.KOR12), any()))
			.willReturn(parsingAnonymousDto);

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(VALID_PARSING_TEXT);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.UNKNOWN_ERROR);
		assertThat(result.getFailureDetails()).contains("모든 검증 단계를 통과했지만 실패로 기록되었습니다");
	}

	@DisplayName("기존 실패 데이터 재분석 - failureReason이 null인 데이터 분석")
	@Test
	void analyzeExistingFailures() {
		//given
		User user = createUser();
		ParsingTextHistory history1 = ParsingTextHistory.builder()
			.id(1L)
			.user(user)
			.parsingText("parsingText1")
			.parsingResult(ParsingResult.FAIL)
			.failureReason(null)
			.failureDetails(null)
			.build();

		ParsingTextHistory history2 = ParsingTextHistory.builder()
			.id(2L)
			.user(user)
			.parsingText("parsingText2")
			.parsingResult(ParsingResult.FAIL)
			.failureReason(null)
			.failureDetails(null)
			.build();

		List<ParsingTextHistory> existingFailures = List.of(history1, history2);

		given(queryParsingTextHistoryPort.findByParsingResultAndFailureReasonIsNull(PageRequest.of(0, 100)))
			.willReturn(existingFailures)
			.willReturn(Collections.emptyList());

		//when
		int analyzedCount = failureAnalysisService.analyzeExistingFailures();

		//then
		assertThat(analyzedCount).isEqualTo(2);
		then(saveParsingTextHistoryPort).should(times(2)).saveParsingTextHistory(any());
	}

	private User createAnonymousUser() {
		return User.builder()
			.id(1L)
			.name("홍길동")
			.studentNumber("60191000")
			.entryYear(19)
			.englishLevel(EnglishLevel.ENG12)
			.koreanLevel(KoreanLevel.KOR12)
			.primaryMajor("경영학전공")
			.studentCategory(StudentCategory.NORMAL)
			.build();
	}

	private User createUser() {
		return User.builder()
			.id(1L)
			.authId("test")
			.password("password")
			.name("테스트")
			.studentNumber("60191000")
			.entryYear(19)
			.englishLevel(EnglishLevel.ENG12)
			.koreanLevel(KoreanLevel.KOR12)
			.primaryMajor("경영학전공")
			.studentCategory(StudentCategory.NORMAL)
			.build();
	}
}