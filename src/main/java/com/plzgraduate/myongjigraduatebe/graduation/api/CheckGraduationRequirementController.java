package com.plzgraduate.myongjigraduatebe.graduation.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.request.CheckGraduationRequirementRequest;
import com.plzgraduate.myongjigraduatebe.graduation.api.dto.response.DetailGraduationResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@WebAdapter
@RequestMapping("/api/v1/graduations/check")
@RequiredArgsConstructor
public class CheckGraduationRequirementController implements CheckGraduationRequirementApiPresentation {

	@PostMapping
	public DetailGraduationResultResponse checkGraduationRequirement(
		CheckGraduationRequirementRequest checkGraduationRequirementRequest
	) {
		return null;
	}
}
