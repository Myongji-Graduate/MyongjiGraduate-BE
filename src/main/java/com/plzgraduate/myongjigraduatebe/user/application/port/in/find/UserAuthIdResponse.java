package com.plzgraduate.myongjigraduatebe.user.application.port.in.find;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserAuthIdResponse {

	private String authId;
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
