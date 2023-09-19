package com.plzgraduate.myongjigraduatebe.auth.application.port.out;

import java.util.Optional;

import javax.swing.text.html.Option;

import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;

public interface FindRefreshTokenPort {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
