package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

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

	@InjectMocks
	private FailureAnalysisService failureAnalysisService;

	@DisplayName("졸업 검사 실패 - 전공명 불일치 (IllegalArgumentException: 소속 단과대가 존재하지 않습니다)")
	@Test
	void analyzeFailure_majorNameMismatch() {
		//given
		String parsingText = "출력일자 :  2023/09/01|1/1"
			+ "|존재하지않는대학 존재하지않는전공, 홍길동(60191000), 현학적 - 재학, 이수 - 6, 입학 - 신입학(2019/03/04)"
			+ "|토익 - 625, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2021/07/20)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 15, 핵심교양 12, 학문기초교양 6, 일반교양 22, 전공 50, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 0"
			+ "|총 취득학점 - 105, 총점 - 368.5, 평균평점 - 4.14"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|";

		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(EnglishLevel.ENG12, KoreanLevel.KOR12, parsingText))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(parsingText);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.MAJOR_NAME_MISMATCH);
		assertThat(result.getFailureDetails()).contains("전공명이 시스템에 등록되지 않았습니다");
		assertThat(result.getFailureDetails()).contains("소속 단과대가 존재하지 않습니다");
	}

	@DisplayName("졸업 검사 실패 - 졸업 요건 없음 (NoSuchElementException: 일치하는 졸업 요건이 존재하지 않습니다)")
	@Test
	void analyzeFailure_graduationRequirementNotFound() {
		//given
		String parsingText = "출력일자 :  2023/09/01|1/1"
			+ "|경영대학 경영학과, 이아현(60191000), 현학적 - 재학, 이수 - 6, 입학 - 신입학(2019/03/04)"
			+ "|토익 - 625, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2021/07/20)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 15, 핵심교양 12, 학문기초교양 6, 일반교양 22, 전공 50, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 0"
			+ "|총 취득학점 - 105, 총점 - 368.5, 평균평점 - 4.14"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|";

		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(EnglishLevel.ENG12, KoreanLevel.KOR12, parsingText))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new NoSuchElementException("일치하는 졸업 요건이 존재하지 않습니다."));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(parsingText);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.GRADUATION_REQUIREMENT_NOT_FOUND);
		assertThat(result.getFailureDetails()).contains("전공명/입학년도 조합에 맞는 졸업 요건을 찾을 수 없습니다");
		assertThat(result.getFailureDetails()).contains("일치하는 졸업 요건이 존재하지 않습니다");
	}

	@DisplayName("졸업 검사 실패 - 일반 오류 (기타 Exception)")
	@Test
	void analyzeFailure_generalGraduationCheckFailed() {
		//given
		String parsingText = "출력일자 :  2023/09/01|1/1"
			+ "|경영대학 경영학과, 이아현(60191000), 현학적 - 재학, 이수 - 6, 입학 - 신입학(2019/03/04)"
			+ "|토익 - 625, 영어교과목면제 - 면제없음, 최종학적변동 - 불일치복학(2021/07/20)"
			+ "|편입생 인정학점 - 교양 0, 전공 0, 자유선택 0, 성경과인간이해 0"
			+ "|교환학생 인정학점 - 학문기초교양 0, 일반교양 0, 전공 0, 복수전공학문기초교양 0, 복수전공 0, 연계전공 0, 부전공 0, 자유선택 0"
			+ "|공통교양 15, 핵심교양 12, 학문기초교양 6, 일반교양 22, 전공 50, 복수전공 0, 연계전공 0, 부전공 0, 교직 0, 자유선택 0"
			+ "|총 취득학점 - 105, 총점 - 368.5, 평균평점 - 4.14"
			+ "|이수구분|수강년도/학기|한글코드|과목코드|과목명|학점|등급|중복|";

		User anonymous = createAnonymousUser();
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		ParsingAnonymousDto parsingAnonymousDto = new ParsingAnonymousDto(anonymous, takenLectureInventory);

		given(parsingAnonymousUseCase.parseAnonymous(EnglishLevel.ENG12, KoreanLevel.KOR12, parsingText))
			.willReturn(parsingAnonymousDto);
		given(checkGraduationRequirementUseCase.checkGraduationRequirement(anonymous, takenLectureInventory))
			.willThrow(new RuntimeException("예상치 못한 오류"));

		//when
		FailureAnalysisService.FailureAnalysisResult result = failureAnalysisService.analyzeFailure(parsingText);

		//then
		assertThat(result.getFailureReason()).isEqualTo(FailureReason.GRADUATION_CHECK_FAILED);
		assertThat(result.getFailureDetails()).contains("졸업 검사 중 오류 발생");
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
		then(saveParsingTextHistoryPort).should(times(2)).saveParsingTextHistory(org.mockito.ArgumentMatchers.any());
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
			.studentCategory(com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.NORMAL)
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
			.studentCategory(com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.NORMAL)
			.build();
	}
}

