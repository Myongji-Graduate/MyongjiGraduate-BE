package com.plzgraduate.myongjigraduatebe.parsing.application.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.SaveParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UseCase
@RequiredArgsConstructor
class ParsingTextHistoryService {

	private final SaveParsingTextHistoryPort saveParsingTextHistoryPort;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveParsingTextHistory(ParsingTextHistory history) {
		try {
			saveParsingTextHistoryPort.saveParsingTextHistory(history);
		} catch (Exception e) {
			log.warn("fail to save parsing history");
		}
	}
}
