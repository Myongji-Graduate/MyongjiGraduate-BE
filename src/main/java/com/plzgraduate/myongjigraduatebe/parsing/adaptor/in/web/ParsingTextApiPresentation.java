package com.plzgraduate.myongjigraduatebe.parsing.adaptor.in.web;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ParsingText", description = "파싱 텍스트를 등록하는 API")
public interface ParsingTextApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	void enrollParsingText(@LoginUser Long userId, @Valid @RequestBody ParsingTextRequest parsingTextRequest);
}
