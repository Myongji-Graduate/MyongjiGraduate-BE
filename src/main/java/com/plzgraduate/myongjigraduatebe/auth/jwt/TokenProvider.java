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
import com.plzgraduate.myongjigraduatebe.auth.jwt.CustomUserDetails;
import com.plzgraduate.myongjigraduatebe.auth.jwt.JwtProperties;

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
		return new UsernamePasswordAuthenticationToken(
			claims.id, token, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
	}

	private String makeToken(Authentication authentication, Date expiry) {
		Date now = new Date();

		return JWT.create()
			.withIssuer(jwtProperties.getIssuer())
			.withIssuedAt(now)
			.withExpiresAt(expiry)
			.withClaim("id", getUserId(authentication))
			.sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));

	}

	private Long getUserId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails)authentication.getPrincipal();
		return principal.getUserId();
	}

	private Claims verifyToken(String token) {
		JWTVerifier jwtverifier = JWT.require(algorithm).withIssuer(jwtProperties.getIssuer()).build();
		return new Claims(jwtverifier.verify(token));
	}

	static public class Claims {
		Long id;
		Date iat;
		Date exp;

		Claims(DecodedJWT decodedJWT) {
			Claim id = decodedJWT.getClaim("id");
			if (!id.isNull()) {
				this.id = id.asLong();
			}
			this.iat = decodedJWT.getIssuedAt();
			this.exp = decodedJWT.getExpiresAt();
		}
	}
}
