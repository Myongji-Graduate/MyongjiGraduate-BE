package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.DeleteParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.mapper.ParsingTextHistoryMapper;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.repository.ParsingTextRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class ParsingTextHistoryAdapter implements SaveParsingTextHistoryPort, DeleteParsingTextHistoryPort {

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
