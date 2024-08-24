package com.plzgraduate.myongjigraduatebe.user.api.findauthid.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserAuthIdResponse {

	@Schema(name = "authId", example = "plzgraduate")
	private String authId;
	@Schema(name = "studentNumber", example = "60202000")
	private String studentNumber;

	@Builder
	private UserAuthIdResponse(String authId, String studentNumber) {
		this.authId = authId;
		this.studentNumber = studentNumber;
	}

	public static UserAuthIdResponse of(String encryptedAutId, String studentNumber) {
		return UserAuthIdResponse.builder()
			.authId(encryptedAutId)
			.studentNumber(studentNumber).build();
	}
}
