package com.plzgraduate.myongjigraduatebe.user.api.withdraw;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.user.api.withdraw.dto.request.WithDrawRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "WithDraw", description = "유저의 회원 탈퇴 요청을 수행하는 API")
public interface WithDrawApiPresentation {

	@Parameter(name = "userId", hidden = true)
	void withDraw(@LoginUser Long userId, @RequestBody WithDrawRequest withDrawRequest);
}
