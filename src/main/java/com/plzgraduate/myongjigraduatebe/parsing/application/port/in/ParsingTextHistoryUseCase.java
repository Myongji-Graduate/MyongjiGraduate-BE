package com.plzgraduate.myongjigraduatebe.parsing.application.port.in;

import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.command.ParsingTextCommand;

public interface ParsingTextHistoryUseCase {
	void saveParsingTextHistoryIfSuccess(ParsingTextCommand parsingTextCommand);

	void saveParsingTextHistoryIfFail(ParsingTextCommand parsingTextCommand);
}
