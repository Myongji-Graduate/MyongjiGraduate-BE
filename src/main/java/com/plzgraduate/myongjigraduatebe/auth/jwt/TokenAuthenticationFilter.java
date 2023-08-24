package com.plzgraduate.myongjigraduatebe.auth.jwt;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.plzgraduate.myongjigraduatebe.auth.CustomUserDetails;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.LoadUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;

	private final static String HEADER_AUTHORIZATION = "Authorization";
	private final static String TOKEN_PREFIX = "Bearer ";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		if(SecurityContextHolder.getContext().getAuthentication() == null) {
			String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
			String token = getAccessToken(authorizationHeader);
			if(authorizationHeader != null) {
				try {
					Authentication authentication = tokenProvider.getAuthentication(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);
				} catch (Exception e) {
					log.warn("Jwt processing failed: {}", e.getMessage());
				}
			}
		} else {
			log.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
				SecurityContextHolder.getContext().getAuthentication());
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