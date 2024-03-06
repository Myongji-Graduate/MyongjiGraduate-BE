package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

public interface ParsingTextHistoryUseCase {
	void saveParsingTextHistoryIfSuccess(ParsingTextCommand parsingTextCommand);

	void saveParsingTextHistoryIfFail(ParsingTextCommand parsingTextCommand);
}
