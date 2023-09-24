package com.plzgraduate.myongjigraduatebe.user.application.port.in.check;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthIdDuplicationResponse {

	private String authId;

	private boolean notDuplicated;

	@Builder
	private AuthIdDuplicationResponse(String authId, boolean notDuplicated) {
		this.authId = authId;
		this.notDuplicated = notDuplicated;
	}
}
