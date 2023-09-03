package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ParsingTextHistory {

	private final Long id;
	private final User user;
	private final String parsingText;
	private ParsingResult parsingResult;

	@Builder
	private ParsingTextHistory(Long id, User user, String parsingText, ParsingResult parsingResult) {
		this.id = id;
		this.user = user;
		this.parsingText = parsingText;
		this.parsingResult = parsingResult;
	}
	public static ParsingTextHistory success(User user, String parsingText) {
		return ParsingTextHistory.builder()
			.user(user)
			.parsingText(parsingText)
			.parsingResult(ParsingResult.SUCCESS)
			.build();
	}

	public static ParsingTextHistory fail(User user, String parsingText) {
		return ParsingTextHistory.builder()
			.user(user)
			.parsingText(parsingText)
			.parsingResult(ParsingResult.FAIL)
			.build();
	}

}
