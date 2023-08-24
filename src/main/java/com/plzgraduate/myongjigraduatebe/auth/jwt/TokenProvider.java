package com.plzgraduate.myongjigraduatebe.auth.jwt;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.plzgraduate.myongjigraduatebe.auth.CustomUserDetails;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Component
public class TokenProvider {
	private final JwtProperties jwtProperties;
	private final Algorithm algorithm;

	public TokenProvider(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.algorithm = Algorithm.HMAC256(jwtProperties.getSecretKey());
	}

	public String generateToken(Authentication authentication) {
		Date now = new Date();
		return makeToken(authentication, new Date(now.getTime() + jwtProperties.getExpirySeconds()));
	}

	private String makeToken(Authentication authentication, Date expiry) {
		Date now = new Date();

		return JWT.create()
			.withIssuer(jwtProperties.getIssuer())
			.withIssuedAt(now)
			.withExpiresAt(expiry)
			.withClaim("id", getId(authentication))
			.sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));

	}

	private Long getId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails)authentication.getPrincipal();
		return principal.getUserKey();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = verifyToken(token);
		return new UsernamePasswordAuthenticationToken(
			claims.id, token, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
	}

	private Claims verifyToken(String token) {
		JWTVerifier jwtverifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
		return new Claims(jwtverifier.verify(token));
	}

	static public class Claims {
		Long id;
		String userId;
		Date iat;
		Date exp;

		Claims(DecodedJWT decodedJWT) {
			Claim id = decodedJWT.getClaim("id");
			if (!id.isNull()) {
				this.id = id.asLong();
			}
			Claim userId = decodedJWT.getClaim("userId");
			if (!userId.isNull()) {
				this.userId = userId.asString();
			}
			this.iat = decodedJWT.getIssuedAt();
			this.exp = decodedJWT.getExpiresAt();
		}
	}
}
