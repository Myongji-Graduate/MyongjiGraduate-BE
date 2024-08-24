package com.plzgraduate.myongjigraduatebe.user.api.findauthid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.web.bind.annotation.PathVariable;

import com.plzgraduate.myongjigraduatebe.user.api.findauthid.dto.response.UserAuthIdResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FindAuthId", description = "학번으로 해당 학생의 아이디를 조회하는 API")
public interface FindAuthIdApiPresentation {

	UserAuthIdResponse findUserAuthId(
		@Parameter(name = "studentNumber", description = "학번", in = ParameterIn.PATH) @PathVariable @Pattern(regexp = "^60\\d{6}$", message = "INVALIDATED_STUDENT_NUMBER_TYPE") String studentNumber);
}
