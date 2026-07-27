package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;

public interface ParsingAnonymousUseCase {

	ParsingAnonymousDto parseAnonymous(
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel,
		String parsingText,
		boolean honorsCollege,
		String honorsTargetMajor
	);

	default ParsingAnonymousDto parseAnonymous(
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel,
		String parsingText,
		boolean honorsCollege
	) {
		return parseAnonymous(englishLevel, koreanLevel, parsingText, honorsCollege, null);
	}

	default ParsingAnonymousDto parseAnonymous(
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel,
		String parsingText
	) {
		return parseAnonymous(englishLevel, koreanLevel, parsingText, false, null);
	}
}
