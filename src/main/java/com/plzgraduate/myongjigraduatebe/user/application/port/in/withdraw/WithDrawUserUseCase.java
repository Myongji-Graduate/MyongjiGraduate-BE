package com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw;

public interface WithDrawUserUseCase {

	void withDraw(Long userId, WithDrawCommand withDrawCommand);
}
