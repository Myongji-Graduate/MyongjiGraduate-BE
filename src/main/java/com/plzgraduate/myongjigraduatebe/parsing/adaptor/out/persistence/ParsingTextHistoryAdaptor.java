package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.DeleteParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class ParsingTextHistoryAdaptor implements SaveParsingTextHistoryPort, DeleteParsingTextHistoryPort {

	private final ParsingTextRepository parsingTextRepository;
	private final ParsingTextHistoryMapper parsingTextHistoryMapper;
	@Override
	public void saveParsingTextHistory(ParsingTextHistory parsingTextHistory) {
		parsingTextRepository.save(parsingTextHistoryMapper.mapToJpaEntity(parsingTextHistory));
	}

	@Override
	public void deleteUserParsingTextHistory(User user) {
		parsingTextRepository.deleteAllByUser(parsingTextHistoryMapper.mapToUserJpaEntity(user));
	}
}
