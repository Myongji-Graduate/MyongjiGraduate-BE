package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.withdraw;

import javax.validation.constraints.NotBlank;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw.WithDrawCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WithDrawRequest {

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Schema(name = "password", example = "Plz1231343!")
	private String password;

	@Builder
	private WithDrawRequest(String password) {
		this.password = password;
	}

	public WithDrawCommand toCommand() {
		return WithDrawCommand.builder()
			.password(this.password).build();
	}
}