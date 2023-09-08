package com.plzgraduate.myongjigraduatebe.auth.jwt;

import java.util.Collections;
import java.util.Date;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

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

	public Authentication getAuthentication(String token) {
		Claims claims = verifyToken(token);
		return new JwtAuthenticationToken(
			new AuthenticationUser(claims.id, claims.authId), null,
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
	}

	private String makeToken(Authentication authentication, Date expiry) {
		Date now = new Date();

		return JWT.create()
			.withIssuer(jwtProperties.getIssuer())
			.withIssuedAt(now)
			.withExpiresAt(expiry)
			.withClaim("id", getUserId(authentication))
			.withClaim("authId", getAuthId(authentication))
			.sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));

	}

	private Long getUserId(Authentication authentication) {
		AuthenticationUser principal = (AuthenticationUser)authentication.getPrincipal();
		return principal.getId();
	}

	private String getAuthId(Authentication authentication) {
		AuthenticationUser principal = (AuthenticationUser)authentication.getPrincipal();
		return principal.getAuthId();
	}

	private Claims verifyToken(String token) {
		JWTVerifier jwtverifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
		return new Claims(jwtverifier.verify(token));
	}

	static class Claims {
		Long id;
		String authId;
		Date iat;
		Date exp;

		Claims(DecodedJWT decodedJWT) {
			Claim claimId = decodedJWT.getClaim("id");
			Claim claimAuthId = decodedJWT.getClaim("authId");
			if (!claimId.isNull()) {
				this.id = claimId.asLong();
			}
			if (!claimAuthId.isNull()) {
				this.authId = claimAuthId.asString();
			}
			this.iat = decodedJWT.getIssuedAt();
			this.exp = decodedJWT.getExpiresAt();
		}
	}
}
