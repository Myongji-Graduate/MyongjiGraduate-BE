package com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextCommand;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.in.ParsingTextUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/parsing-text")
@RequiredArgsConstructor
@Tag(name = "ParsingText", description = "파싱 텍스트를 등록하는 API")
public class ParsingTextController {

	private final ParsingTextUseCase parsingTextUseCase;
	private final ParsingTextHistoryUseCase parsingTextHistoryUseCase;

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@PostMapping
	public void enrollParsingText(@LoginUser Long userId, @Valid @RequestBody ParsingTextRequest parsingTextRequest) {
		ParsingTextCommand command = parsingTextRequest.toCommand(userId);
		try {
			parsingTextUseCase.enrollParsingText(command);
			parsingTextHistoryUseCase.saveParsingTextHistoryIfSuccess(command);
		} catch (Exception e) {
			parsingTextHistoryUseCase.saveParsingTextHistoryIfFail(command);
			throw e;
		}
	}
}
