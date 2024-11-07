package com.plzgraduate.myongjigraduatebe.auth.application.port;

public interface SaveRefreshTokenPort {

	void saveRefreshToken(String refreshToken, Long userId);
}
