package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.withdraw;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw.WithDrawUserUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class WithDrawController {

	private final WithDrawUserUseCase withDrawUserUseCase;

	@DeleteMapping()
	public void withDraw(@LoginUser Long userId) {
		withDrawUserUseCase.withDraw(userId);
	}
}
