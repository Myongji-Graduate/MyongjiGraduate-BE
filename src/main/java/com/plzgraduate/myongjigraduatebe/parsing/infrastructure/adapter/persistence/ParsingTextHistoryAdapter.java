package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.DeleteParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.QueryParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.mapper.ParsingTextHistoryMapper;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.repository.ParsingTextRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@PersistenceAdapter
@RequiredArgsConstructor
public class ParsingTextHistoryAdapter implements SaveParsingTextHistoryPort,
	DeleteParsingTextHistoryPort, QueryParsingTextHistoryPort {

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

	@Override
	public List<ParsingTextHistory> findByParsingResultAndFailureReasonIsNull() {
		return parsingTextRepository.findByParsingResultAndFailureReasonIsNull(ParsingResult.FAIL)
			.stream()
			.map(parsingTextHistoryMapper::mapToDomainEntity)
			.collect(Collectors.toList());
	}

	@Override
	public List<ParsingTextHistory> findByParsingResultAndFailureReasonIsNull(Pageable pageable) {
		return parsingTextRepository.findByParsingResultAndFailureReasonIsNull(ParsingResult.FAIL, pageable)
			.stream()
			.map(parsingTextHistoryMapper::mapToDomainEntity)
			.collect(Collectors.toList());
	}

	@Override
	public List<ParsingTextHistory> findByParsingResult(ParsingResult parsingResult) {
		return parsingTextRepository.findByParsingResult(parsingResult)
			.stream()
			.map(parsingTextHistoryMapper::mapToDomainEntity)
			.collect(Collectors.toList());
	}
}
