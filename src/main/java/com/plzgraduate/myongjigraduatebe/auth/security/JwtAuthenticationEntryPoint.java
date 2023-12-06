package com.plzgraduate.myongjigraduatebe.auth.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.core.exception.ExceptionResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
	private final static ExceptionResponse E401 = ExceptionResponse.of(HttpStatus.UNAUTHORIZED, "Authentication error (cause: unauthorized)");

	private final ObjectMapper om;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("content-type", "application/json");
		response.getWriter().write(om.writeValueAsString(E401));
		response.getWriter().flush();
		response.getWriter().close();
	}
}
