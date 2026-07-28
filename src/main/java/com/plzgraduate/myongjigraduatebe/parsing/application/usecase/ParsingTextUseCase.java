package com.plzgraduate.myongjigraduatebe.parsing.application.usecase;

public interface ParsingTextUseCase {

	void enrollParsingText(Long userId, String parsingText, boolean honorsCollege, String honorsTargetMajor);

	default void enrollParsingText(Long userId, String parsingText, boolean honorsCollege) {
		enrollParsingText(userId, parsingText, honorsCollege, null);
	}

	default void enrollParsingText(Long userId, String parsingText) {
		enrollParsingText(userId, parsingText, false, null);
	}
}
