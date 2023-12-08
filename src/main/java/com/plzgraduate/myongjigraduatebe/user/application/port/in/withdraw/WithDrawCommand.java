package com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WithDrawCommand {

	private final String password;

	@Builder
	private WithDrawCommand(String password) {
		this.password = password;
	}
}
