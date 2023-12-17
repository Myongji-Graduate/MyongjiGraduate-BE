package com.plzgraduate.myongjigraduatebe.user.application.port.in.check;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class AuthIdDuplicationResponse {

	private final String authId;

	private final boolean notDuplicated;

	@Builder
	private AuthIdDuplicationResponse(String authId, boolean notDuplicated) {
		this.authId = authId;
		this.notDuplicated = notDuplicated;
	}
}
