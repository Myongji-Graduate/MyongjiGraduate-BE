package com.plzgraduate.myongjigraduatebe.user.api.withdraw;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.user.api.withdraw.dto.request.WithDrawRequest;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.withdraw.WithDrawUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@WebAdapter
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class WithDrawController implements WithDrawApiPresentation {

	private final WithDrawUserUseCase withDrawUserUseCase;

	@DeleteMapping()
	public void withDraw(@LoginUser Long userId, @RequestBody WithDrawRequest withDrawRequest) {
		withDrawUserUseCase.withDraw(userId, withDrawRequest.getPassword());
	}
}
