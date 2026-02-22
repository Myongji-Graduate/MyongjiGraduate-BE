package com.plzgraduate.myongjigraduatebe.parsing.application.port;

import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface QueryParsingTextHistoryPort {

	List<ParsingTextHistory> findByParsingResultAndFailureReasonIsNull(Pageable pageable);

}

