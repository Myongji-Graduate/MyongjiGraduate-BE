package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
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
