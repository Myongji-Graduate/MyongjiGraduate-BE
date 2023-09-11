package com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web;

import javax.validation.constraints.NotNull;

import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextCommand;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
class ParsingTextRequest {

	@NotNull(message = "parsingText는 null값이 될 수 없습니다.")
	private String parsingText;

	@Builder
	private ParsingTextRequest(String parsingText) {
		this.parsingText = parsingText;
	}

	public ParsingTextCommand toCommand(Long userId) {
		return ParsingTextCommand.builder()
			.userId(userId)
			.parsingText(parsingText)
			.build();
	}
}
