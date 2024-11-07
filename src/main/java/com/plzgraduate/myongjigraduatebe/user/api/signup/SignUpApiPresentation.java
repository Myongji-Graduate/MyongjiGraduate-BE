package com.plzgraduate.myongjigraduatebe.user.api.signup;

import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.request.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.StudentNumberDuplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "SignUp", description = "회원가입을 진행하는 API")
public interface SignUpApiPresentation {

	@Operation(description = "회원가입을 진행한다.")
	void signUp(@Valid @RequestBody SignUpRequest signUpRequest);

	@Operation(description = "로그인 아이디 중복 여부를 체크한다.")
	@Parameter(name = "auth-id", description = "아이디")
	AuthIdDuplicationResponse checkAuthIdDuplication(
		@RequestParam("auth-id") @Size(min = 6, max = 20, message = "INVALIDATED_AUTHID_TYPE") String authId);

	@Operation(description = "학번 중복 여부를 체크한다.")
	@Parameter(name = "student-number", description = "학번")
	StudentNumberDuplicationResponse checkStudentNumberDuplication(
		@RequestParam("student-number") @Pattern(regexp = "^60\\d{6}$", message = "INVALIDATED_STUDENT_NUMBER_TYPE") String studentNumber);
}
