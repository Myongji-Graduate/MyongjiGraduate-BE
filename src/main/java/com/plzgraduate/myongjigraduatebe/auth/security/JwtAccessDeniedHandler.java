package com.plzgraduate.myongjigraduatebe.auth.security;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.exception.ExceptionResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private static final ExceptionResponse E403 = ExceptionResponse.from(AUTHENTICATION_FAIL_FORBIDDEN.toString());

	private final ObjectMapper om;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setHeader("content-type", "application/json");
		response.getWriter().write(om.writeValueAsString(E403));
		response.getWriter().flush();
		response.getWriter().close();
	}
}
