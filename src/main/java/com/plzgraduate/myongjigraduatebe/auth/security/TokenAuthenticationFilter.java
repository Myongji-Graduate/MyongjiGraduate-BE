package com.plzgraduate.myongjigraduatebe.auth.security;

import java.io.IOException;
import java.util.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";
	private final TokenProvider tokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		if (SecurityContextHolder.getContext()
			.getAuthentication() == null) {
			String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
			String token = getAccessToken(authorizationHeader);
			if (token != null) {
				try {
					Long userId = tokenProvider.extractUserId(token);
					Authentication authentication = new JwtAuthenticationToken(
						userId, null,
						Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
					SecurityContextHolder.getContext()
						.setAuthentication(authentication);
				} catch (Exception e) {
					log.warn("Jwt processing failed: {}", e.getMessage());
				}
			}
		} else {
			log.debug(
				"SecurityContextHolder not populated with security token, as it already contained: '{}'",
				SecurityContextHolder.getContext()
					.getAuthentication());
		}
		filterChain.doFilter(request, response);

	}

	private String getAccessToken(String authorizationHeader) {
		if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
			return authorizationHeader.substring(TOKEN_PREFIX.length());
		}
		return null;
	}
}
