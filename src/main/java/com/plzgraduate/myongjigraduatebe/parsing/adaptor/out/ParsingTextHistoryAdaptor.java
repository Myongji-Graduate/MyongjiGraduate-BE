package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class ParsingTextHistoryAdaptor implements SaveParsingTextHistoryPort {

	private final ParsingTextRepository parsingTextRepository;
	private final ParsingTextHistoryMapper parsingTextHistoryMapper;
	@Override
	public void saveParsingTextHistory(ParsingTextHistory parsingTextHistory) {
		parsingTextRepository.save(parsingTextHistoryMapper.mapToJpaEntity(parsingTextHistory));
	}
}
