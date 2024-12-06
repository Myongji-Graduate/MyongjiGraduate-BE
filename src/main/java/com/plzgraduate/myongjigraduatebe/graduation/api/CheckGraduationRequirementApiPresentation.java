package com.plzgraduate.myongjigraduatebe.graduation.api;

import com.plzgraduate.myongjigraduatebe.graduation.api.dto.request.CheckGraduationRequirementRequest;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.CheckGraduationRequirementResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "CheckGraduationRequirement", description = "로그인 없이 졸업 요건을 검사하는 API")
public interface CheckGraduationRequirementApiPresentation {

	CheckGraduationRequirementResponse checkGraduationRequirement(
		@Valid @RequestBody CheckGraduationRequirementRequest checkGraduationRequirementRequest
	);
}
