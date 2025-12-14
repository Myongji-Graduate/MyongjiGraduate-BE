package com.plzgraduate.myongjigraduatebe.parsing.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.parsing.api.dto.request.ParsingTextRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "ParsingText", description = "파싱 텍스트를 등록하는 API")
public interface ParsingTextApiPresentation {

	@SecurityRequirement(name = "AccessToken")
	void enrollParsingText(@Parameter(hidden = true) @LoginUser Long userId,
		@Valid @RequestBody ParsingTextRequest parsingTextRequest);
}
