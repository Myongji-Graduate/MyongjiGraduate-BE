package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.Getter;

@Getter
public class CheckGraduationRequirementResponse {

	private final User anonymous;
	private final GraduationResult graduationResult;

	public CheckGraduationRequirementResponse(
		User anonymous, GraduationResult graduationResult
	) {
		this.anonymous = anonymous;
		this.graduationResult = graduationResult;
	}
}
