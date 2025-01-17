package com.plzgraduate.myongjigraduatebe.user.api.withdraw;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.user.api.withdraw.dto.request.WithDrawRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "WithDraw", description = "유저의 회원 탈퇴 요청을 수행하는 API")
public interface WithDrawApiPresentation {

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	void withDraw(@LoginUser Long userId, @RequestBody WithDrawRequest withDrawRequest);
}
