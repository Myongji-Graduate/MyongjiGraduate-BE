package com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web;

import javax.validation.constraints.NotBlank;

import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.command.ParsingTextCommand;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParsingTextRequest {

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
