package com.plzgraduate.myongjigraduatebe.parsing.application.port.in;

public interface ParsingTextHistoryUseCase {
	void saveParsingTextHistoryIfSuccess(ParsingTextCommand parsingTextCommand);

	void saveParsingTextHistoryIfFail(ParsingTextCommand parsingTextCommand);
}
