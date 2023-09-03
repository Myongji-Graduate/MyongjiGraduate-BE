package com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/parsing-text")
@RequiredArgsConstructor
class ParsingTextController {

	private final ParsingTextUseCase parsingTextUseCase;

	@PostMapping
	public void enrollParsingText(@LoginUser Long userId, @Valid @RequestBody ParsingTextRequest parsingTextRequest) {
		parsingTextUseCase.enrollParsingText(parsingTextRequest.toCommand(userId));
	}
}
