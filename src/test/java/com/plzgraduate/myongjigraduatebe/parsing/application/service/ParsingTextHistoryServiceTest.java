package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextCommand;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class ParsingTextHistoryServiceTest {
	@Mock
	private SaveParsingTextHistoryPort saveParsingTextHistoryPort;
	@Mock
	private FindUserUseCase findUserUseCase;
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
		ParsingTextCommand command = ParsingTextCommand.builder()
			.userId(userId)
			.parsingText(parsingText)
			.build();
		given(findUserUseCase.findUserById(userId)).willReturn(user);
		ArgumentCaptor<ParsingTextHistory> captor = ArgumentCaptor.forClass(ParsingTextHistory.class);
		//when
		parsingTextHistoryService.generateSucceedParsingTextHistory(command);

		//then
		then(saveParsingTextHistoryPort).should().saveParsingTextHistory(captor.capture());
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
		ParsingTextCommand command = ParsingTextCommand.builder()
			.userId(userId)
			.parsingText(parsingText)
			.build();
		given(findUserUseCase.findUserById(userId)).willReturn(user);
		ArgumentCaptor<ParsingTextHistory> captor = ArgumentCaptor.forClass(ParsingTextHistory.class);
		//when
		parsingTextHistoryService.generateFailedParsingTextHistory(command);

		//then
		then(saveParsingTextHistoryPort).should().saveParsingTextHistory(captor.capture());
		ParsingTextHistory captureArgument = captor.getValue();
		assertThat(captureArgument.getUser()).isEqualTo(user);
		assertThat(captureArgument.getParsingText()).isEqualTo(parsingText);
	}

	private User createUser(Long id, String authId, String password, EnglishLevel englishLevel, String name,
		String studentNumber, int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		return User.builder()
			.id(id)
			.authId(authId)
			.password(password)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.englishLevel(englishLevel)
			.major(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}
}
