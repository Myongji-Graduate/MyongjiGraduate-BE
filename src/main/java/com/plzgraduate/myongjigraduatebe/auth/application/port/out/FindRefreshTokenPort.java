package com.plzgraduate.myongjigraduatebe.auth.application.port.out;

import java.util.Optional;

public interface FindRefreshTokenPort {
	Optional<Long> findByRefreshToken(String refreshToken);
}
