package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;

public interface ParsingTextHistoryUseCase {

	void generateSucceedParsingTextHistory(Long userId, String parsingText);

	void generateFailedParsingTextHistory(Long userId, String parsingText);

	String generateAnonymousSucceedParsingTextHistory(String parsingText);

	String generateAnonymousFailedParsingTextHistory(
		String parsingText,
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel
	);
}
