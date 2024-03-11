package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

public interface ParsingTextHistoryUseCase {
	void generateSucceedParsingTextHistory(ParsingTextCommand parsingTextCommand);

	void generateFailedParsingTextHistory(ParsingTextCommand parsingTextCommand);
}
