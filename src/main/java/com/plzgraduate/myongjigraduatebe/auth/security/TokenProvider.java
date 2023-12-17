package com.plzgraduate.myongjigraduatebe.auth.security;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class TokenProvider {
	private final JwtProperties jwtProperties;

	public TokenProvider(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public String generateToken(Long userId) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtProperties.getExpirySeconds());
		return JWT.create()
			.withIssuer(jwtProperties.getIssuer())
			.withIssuedAt(now)
			.withExpiresAt(expiry)
			.withClaim("id", userId)
			.sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
	}

	public String generateRefreshToken() {
		return String.valueOf(UUID.randomUUID());
	}

	public Long extractUserId(String token) {
		JWTVerifier jwtverifier = JWT.require(Algorithm.HMAC256(jwtProperties.getSecretKey())).withIssuer(jwtProperties.getIssuer()).build();
		Claims claims = new Claims(jwtverifier.verify(token));
		return claims.id;
	}

	static class Claims {
		Long id;
		Date iat;
		Date exp;

		Claims(DecodedJWT decodedJWT) {
			Claim claimId = decodedJWT.getClaim("id");
			if (!claimId.isNull()) {
				this.id = claimId.asLong();
			}
			this.iat = decodedJWT.getIssuedAt();
			this.exp = decodedJWT.getExpiresAt();
		}
	}
}
