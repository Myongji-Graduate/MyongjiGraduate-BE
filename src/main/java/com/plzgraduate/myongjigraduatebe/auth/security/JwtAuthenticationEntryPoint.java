package com.plzgraduate.myongjigraduatebe.auth.security;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.AUTHENTICATION_FAIL_UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.core.exception.ExceptionResponse;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final ExceptionResponse E401 = ExceptionResponse.from(
		AUTHENTICATION_FAIL_UNAUTHORIZED.toString());

	private final ObjectMapper om;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("content-type", "application/json");
		response.getWriter()
			.write(om.writeValueAsString(E401));
		response.getWriter()
			.flush();
		response.getWriter()
			.close();
	}
}
