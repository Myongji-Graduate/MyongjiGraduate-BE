package com.plzgraduate.myongjigraduatebe.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.plzgraduate.myongjigraduatebe.auth.PrincipalDetails;
import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.auth.service.JwtAuthService;
import com.plzgraduate.myongjigraduatebe.common.config.JwtConfig;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

  private final JwtAuthService authService;
  private final JwtConfig config;

  @Override
  public void doFilter(
      ServletRequest req,
      ServletResponse res,
      FilterChain chain
  ) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest)req;
    HttpServletResponse response = (HttpServletResponse)res;
    try {
      verifyJwt(request);
    } catch (JWTDecodeException ignored) {
    }

    chain.doFilter(request, response);
  }

  private void verifyJwt(
      HttpServletRequest request
  ) {
    if (SecurityContextHolder
        .getContext()
        .getAuthentication() != null) {
      return;
    }

    String header = request.getHeader(config.getHeader());

    if (header == null || header.isBlank() || !header.startsWith(config.getPrefix())) {
      return;
    }

    String token = header
        .replace(config.getPrefix(), "")
        .trim();

    AuthenticatedUser user = authService.verify(token);

    if (user == null) {
      return;
    }

    PrincipalDetails principalDetails = new PrincipalDetails(user);

    Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

    SecurityContextHolder
        .getContext()
        .setAuthentication(authentication);
  }
}
