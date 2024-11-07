package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

public interface ParsingTextHistoryUseCase {

	void generateSucceedParsingTextHistory(Long userId, String parsingText);

	void generateFailedParsingTextHistory(Long userId, String parsingText);
}
