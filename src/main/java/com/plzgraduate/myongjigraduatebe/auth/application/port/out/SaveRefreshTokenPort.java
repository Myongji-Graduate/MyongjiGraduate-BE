package com.plzgraduate.myongjigraduatebe.auth.application.port.out;


public interface SaveRefreshTokenPort {
	void saveRefreshToken(String refreshToken, Long userId);
}
