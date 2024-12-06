package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;

public interface ParsingAnonymousUseCase {

	ParsingAnonymousDto parseAnonymous(EnglishLevel englishLevel, String parsingText);
}
