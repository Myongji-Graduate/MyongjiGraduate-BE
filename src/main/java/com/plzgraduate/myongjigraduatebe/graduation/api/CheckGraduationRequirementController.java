package com.plzgraduate.myongjigraduatebe.graduation.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.request.CheckGraduationRequirementRequest;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.CheckGraduationRequirementResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CheckGraduationRequirementUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.ParsingAnonymousUseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.usecase.dto.ParsingAnonymousDto;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@WebAdapter
@RequestMapping("/api/v1/graduations/check")
@RequiredArgsConstructor
public class CheckGraduationRequirementController implements CheckGraduationRequirementApiPresentation {

	private final ParsingAnonymousUseCase parsingAnonymousUseCase;
	private final CheckGraduationRequirementUseCase checkGraduationRequirementUseCase;

	@PostMapping
	public CheckGraduationRequirementResponse checkGraduationRequirement(
		@Valid @RequestBody CheckGraduationRequirementRequest checkGraduationRequirementRequest
	) {
		ParsingAnonymousDto parsingAnonymousDto = parsingAnonymousUseCase.parseAnonymous(
			checkGraduationRequirementRequest.getEngLv(),
			checkGraduationRequirementRequest.getParsingText()
		);
		User anonymous = parsingAnonymousDto.getAnonymous();
		GraduationResult graduationResult = checkGraduationRequirementUseCase.checkGraduationRequirement(
			anonymous,
			parsingAnonymousDto.getTakenLectureInventory()
		);

		return new CheckGraduationRequirementResponse(
			anonymous,
			graduationResult
		);
	}
}
