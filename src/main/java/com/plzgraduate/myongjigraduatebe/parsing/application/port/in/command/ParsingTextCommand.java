package com.plzgraduate.myongjigraduatebe.parsing.application.port.in.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParsingTextCommand {
	private Long userId;

	private String parsingText;

	@Builder
	private ParsingTextCommand(Long userId, String parsingText) {
		this.userId = userId;
		this.parsingText = parsingText;
	}
}
