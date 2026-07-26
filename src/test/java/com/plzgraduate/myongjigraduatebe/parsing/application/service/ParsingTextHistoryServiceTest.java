package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingRequesterType;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParsingTextHistoryServiceTest {

	@Mock
	private SaveParsingTextHistoryPort saveParsingTextHistoryPort;
	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FailureAnalysisService failureAnalysisService;
	@InjectMocks
	private ParsingTextHistoryService parsingTextHistoryService;

	@DisplayName("generateSucceedParsingTextHistory 메서드 테스트")
	@Test
	void generateSucceedParsingTextHistory() {
		//given
		Long userId = 1L;
		User user = createUser(userId, "mju1001!", "1q2w3e4r!", EnglishLevel.ENG12, "정지환",
			"60181666", 18, "융합소프트웨어학부", null, StudentCategory.NORMAL);
		String parsingText = "parsingText";
		given(findUserUseCase.findUserById(userId)).willReturn(user);
		ArgumentCaptor<ParsingTextHistory> captor = ArgumentCaptor.forClass(
			ParsingTextHistory.class);
		//when
		parsingTextHistoryService.generateSucceedParsingTextHistory(userId, parsingText);

		//then
		then(saveParsingTextHistoryPort).should()
			.saveParsingTextHistory(captor.capture());
		ParsingTextHistory captureArgument = captor.getValue();
		assertThat(captureArgument.getUser()).isEqualTo(user);
		assertThat(captureArgument.getParsingText()).isEqualTo(parsingText);
	}

	@DisplayName("generateFailedParsingTextHistory 메서드 테스트")
	@Test
	void generateFailedParsingTextHistory() {
		//given
		Long userId = 1L;
		User user = createUser(userId, "mju1001!", "1q2w3e4r!", EnglishLevel.ENG12, "정지환",
			"60181666", 18, "융합소프트웨어학부", null, StudentCategory.NORMAL);
		String parsingText = "parsingText";
		FailureReason failureReason = FailureReason.EMPTY_PARSING_TEXT;
		String failureDetails = "PDF를 인식하지 못했습니다.";
		
		given(findUserUseCase.findUserById(userId)).willReturn(user);
		given(failureAnalysisService.analyzeFailure(
			parsingText, 
			user.getEnglishLevel(), 
			user.getKoreanLevel()
		)).willReturn(new FailureAnalysisService.FailureAnalysisResult(failureReason, failureDetails));
		
		ArgumentCaptor<ParsingTextHistory> captor = ArgumentCaptor.forClass(
			ParsingTextHistory.class);
		//when
		parsingTextHistoryService.generateFailedParsingTextHistory(userId, parsingText);

		//then
		then(saveParsingTextHistoryPort).should()
			.saveParsingTextHistory(captor.capture());
		ParsingTextHistory captureArgument = captor.getValue();
		assertThat(captureArgument.getUser()).isEqualTo(user);
		assertThat(captureArgument.getParsingText()).isEqualTo(parsingText);
		assertThat(captureArgument.getFailureReason()).isEqualTo(failureReason);
		assertThat(captureArgument.getFailureDetails()).isEqualTo(failureDetails);
	}

	@DisplayName("비회원 성공 이력은 사용자 없이 추적 코드와 함께 저장한다.")
	@Test
	void generateAnonymousSucceedParsingTextHistory() {
		ArgumentCaptor<ParsingTextHistory> captor = ArgumentCaptor.forClass(ParsingTextHistory.class);

		String trackingCode = parsingTextHistoryService
			.generateAnonymousSucceedParsingTextHistory("anonymous text");

		then(saveParsingTextHistoryPort).should().saveParsingTextHistory(captor.capture());
		assertThat(trackingCode).isNotBlank();
		assertThat(captor.getValue().getUser()).isNull();
		assertThat(captor.getValue().getTrackingCode()).isEqualTo(trackingCode);
		assertThat(captor.getValue().getRequesterType()).isEqualTo(ParsingRequesterType.ANONYMOUS);
	}

	@DisplayName("비회원 실패 이력은 분석 결과와 추적 코드를 함께 저장한다.")
	@Test
	void generateAnonymousFailedParsingTextHistory() {
		FailureAnalysisService.FailureAnalysisResult analysisResult =
			new FailureAnalysisService.FailureAnalysisResult(
				FailureReason.PARSING_EXCEPTION,
				"파싱 실패"
			);
		given(failureAnalysisService.analyzeFailure(
			"invalid text",
			EnglishLevel.ENG12,
			KoreanLevel.KOR12
		)).willReturn(analysisResult);
		ArgumentCaptor<ParsingTextHistory> captor = ArgumentCaptor.forClass(ParsingTextHistory.class);

		String trackingCode = parsingTextHistoryService.generateAnonymousFailedParsingTextHistory(
			"invalid text",
			EnglishLevel.ENG12,
			KoreanLevel.KOR12
		);

		then(saveParsingTextHistoryPort).should().saveParsingTextHistory(captor.capture());
		assertThat(trackingCode).isNotBlank();
		assertThat(captor.getValue().getUser()).isNull();
		assertThat(captor.getValue().getFailureReason()).isEqualTo(FailureReason.PARSING_EXCEPTION);
		assertThat(captor.getValue().getTrackingCode()).isEqualTo(trackingCode);
		assertThat(captor.getValue().getRequesterType()).isEqualTo(ParsingRequesterType.ANONYMOUS);
	}

	private User createUser(Long id, String authId, String password, EnglishLevel englishLevel,
		String name,
		String studentNumber, int entryYear, String major, String subMajor,
		StudentCategory studentCategory) {
		return User.builder()
			.id(id)
			.authId(authId)
			.password(password)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.englishLevel(englishLevel)
			.koreanLevel(KoreanLevel.KOR12)
			.primaryMajor(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}
}
