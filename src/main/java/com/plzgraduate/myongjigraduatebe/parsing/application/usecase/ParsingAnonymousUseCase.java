package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;

public interface ParsingAnonymousUseCase {

	ParsingAnonymousDto parseAnonymous(
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel,
		String parsingText
	);
}
