package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response.UserInformationResponse;
import lombok.Getter;

@Getter
public class CheckGraduationRequirementResponse {

	private final UserInformationResponse user;
	private final GraduationResult graduationResult;
	private final String trackingCode;

	public CheckGraduationRequirementResponse(
		UserInformationResponse user,
		GraduationResult graduationResult,
		String trackingCode
	) {
		this.user = user;
		this.graduationResult = graduationResult;
		this.trackingCode = trackingCode;
	}
}
