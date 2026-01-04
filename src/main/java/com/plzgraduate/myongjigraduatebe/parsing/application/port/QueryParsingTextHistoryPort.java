package com.plzgraduate.myongjigraduatebe.parsing.application.port;

import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import java.util.List;

public interface QueryParsingTextHistoryPort {

	List<ParsingTextHistory> findByParsingResultAndFailureReasonIsNull();

}

