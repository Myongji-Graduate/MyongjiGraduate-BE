package com.plzgraduate.myongjigraduatebe.parsing.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.api.dto.request.ParsingTextRequest;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextHistoryUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingTextUseCase;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/api/v1/parsing-text")
@RequiredArgsConstructor
public class ParsingTextController implements ParsingTextApiPresentation {

	private final ParsingTextUseCase parsingTextUseCase;
	private final ParsingTextHistoryUseCase parsingTextHistoryUseCase;

	@PostMapping
	public void enrollParsingText(
		@LoginUser Long userId,
		@Valid @RequestBody ParsingTextRequest parsingTextRequest
	) {
		try {
			parsingTextUseCase.enrollParsingText(userId, parsingTextRequest.getParsingText());
			parsingTextHistoryUseCase.generateSucceedParsingTextHistory(
				userId,
				parsingTextRequest.getParsingText()
			);
		} catch (Exception e) {
			parsingTextHistoryUseCase.generateFailedParsingTextHistory(
				userId,
				parsingTextRequest.getParsingText()
			);
			throw e;
		}
	}
}
