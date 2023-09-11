package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextCommand;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
class ParsingTextHistoryService implements ParsingTextHistoryUseCase {

	private final SaveParsingTextHistoryPort saveParsingTextHistoryPort;
	private final FindUserUseCase findUserUseCase;

	@Override
	public void saveParsingTextHistoryIfSuccess(ParsingTextCommand parsingTextCommand) {
		String parsingText = parsingTextCommand.getParsingText();
		User user = findUserUseCase.findUserById(parsingTextCommand.getUserId());
		saveParsingTextHistoryPort.saveParsingTextHistory(ParsingTextHistory.success(user, parsingText));
	}

	@Override
	public void saveParsingTextHistoryIfFail(ParsingTextCommand parsingTextCommand) {
		String parsingText = parsingTextCommand.getParsingText();
		User user = findUserUseCase.findUserById(parsingTextCommand.getUserId());
		saveParsingTextHistoryPort.saveParsingTextHistory(ParsingTextHistory.fail(user, parsingText));
	}
}
