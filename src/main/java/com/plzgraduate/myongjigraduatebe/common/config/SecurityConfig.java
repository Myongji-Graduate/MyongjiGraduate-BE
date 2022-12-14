package com.plzgraduate.myongjigraduatebe.common.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.plzgraduate.myongjigraduatebe.auth.service.JwtAuthService;
import com.plzgraduate.myongjigraduatebe.common.filter.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  public static final String API_V1_PREFIX = "/api/v1";

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      JwtAuthService authService,
      JwtConfig config
  ) throws Exception {
    http
        .authorizeRequests()
        .antMatchers(
            API_V1_PREFIX + "/auth/sign-up", // 회원가입
            API_V1_PREFIX + "/auth/sign-in", // 로그인
            API_V1_PREFIX + "/users/userid-validity-checks", // 유저아이디 중복 체크
            API_V1_PREFIX + "/users/studentNumber-validity-checks", // 학번 중복 체크
            API_V1_PREFIX + "/users/by/student-number/**", // 유저아이디 찾기
            API_V1_PREFIX + "/users/reset-pw", // 비밀번호 재설정
            API_V1_PREFIX + "/users/me/init"  // 토큰 유효성 및 유저 초기화 체크
        )
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .cors()
        .configurationSource(corsConfigurationSource())
        .and()
        /*
          formLogin, csrf, headers, http-basic, rememberMe, logout filter 비활성화
         */
        .formLogin()
        .disable()
        .csrf()
        .disable()
        .headers()
        .disable()
        .httpBasic()
        .disable()
        .rememberMe()
        .disable()
        .logout()
        .disable()
        /*
          Session 사용하지 않음
         */
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        /*
         예외처리 핸들러
         */
        .exceptionHandling()
        .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
        .accessDeniedHandler(accessDeniedHandler())
        .and()
        /*
          Jwt 필터
         */
        .addFilterBefore(authenticationFilter(authService, config), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.addAllowedOriginPattern("*");
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler() {
    return (req, res, e) -> res.setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtAuthenticationFilter authenticationFilter(
      JwtAuthService authService,
      JwtConfig config
  ) {
    return new JwtAuthenticationFilter(authService, config);
  }
}
