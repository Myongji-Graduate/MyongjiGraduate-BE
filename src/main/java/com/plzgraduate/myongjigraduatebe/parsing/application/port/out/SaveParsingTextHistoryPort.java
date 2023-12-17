package com.plzgraduate.myongjigraduatebe.parsing.application.port.out;

import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;

public interface SaveParsingTextHistoryPort {
	void saveParsingTextHistory(ParsingTextHistory parsingTextHistory);
}
