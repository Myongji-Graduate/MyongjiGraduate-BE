package com.plzgraduate.myongjigraduatebe.auth.application.port.out;

import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;

public interface SaveRefreshTokenPort {
	void saveRefreshToken(RefreshToken refreshToken);
}
