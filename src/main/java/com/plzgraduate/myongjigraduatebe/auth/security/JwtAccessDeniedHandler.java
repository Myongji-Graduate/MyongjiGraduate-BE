package com.plzgraduate.myongjigraduatebe.auth.security;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.AUTHENTICATION_FAIL_FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.core.exception.ExceptionResponse;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private static final ExceptionResponse E403 = ExceptionResponse.from(
		AUTHENTICATION_FAIL_FORBIDDEN.toString());

	private final ObjectMapper om;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setHeader("content-type", "application/json");
		response.getWriter()
			.write(om.writeValueAsString(E403));
		response.getWriter()
			.flush();
		response.getWriter()
			.close();
	}
}
