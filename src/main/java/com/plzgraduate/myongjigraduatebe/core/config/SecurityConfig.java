package com.plzgraduate.myongjigraduatebe.core.config;

import com.plzgraduate.myongjigraduatebe.auth.security.JwtAccessDeniedHandler;
import com.plzgraduate.myongjigraduatebe.auth.security.JwtAuthenticationEntryPoint;
import com.plzgraduate.myongjigraduatebe.auth.security.JwtAuthenticationProvider;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenAuthenticationFilter;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

	public static final String API_V1_PREFIX = "/api/v1";

	private final TokenProvider tokenProvider;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(AbstractHttpConfigurer::disable)
	        .formLogin(AbstractHttpConfigurer::disable)
	        .httpBasic(AbstractHttpConfigurer::disable)
	        .rememberMe(AbstractHttpConfigurer::disable)
	        .logout(AbstractHttpConfigurer::disable)
	        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .exceptionHandling(ex -> ex
	            .accessDeniedHandler(jwtAccessDeniedHandler)
	            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
	        )
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                API_V1_PREFIX + "/users/sign-up/**",      // 회원가입
	                API_V1_PREFIX + "/auth/sign-in",          // 로그인
	                API_V1_PREFIX + "/auth/token",            // 새 토큰 발급
	                API_V1_PREFIX + "/users/{student-number}/auth-id", // 아이디 찾기
	                API_V1_PREFIX + "/users/{student-number}/validate", // 유저 검증
	                API_V1_PREFIX + "/users/password",        // 비밀번호 재설정
	                API_V1_PREFIX + "/health",                // 헬스체크
	                API_V1_PREFIX + "/graduations/check",     // 비로그인 졸업 요건 검사
	                API_V1_PREFIX + "/timetable/**",          // 시간표 강의 조회
	                API_V1_PREFIX + "/lectures/popular/**",   // 인기 강의 조회
	                API_V1_PREFIX + "/lectures-info",         // 강좌 정보 조회
	                API_V1_PREFIX + "/lecture-reviews",       // 강좌평 조회
	                "/v3/api-docs/**",
	                "/swagger-ui/**",
	                "/swagger-ui.html",
	                "/api-docs",
	                "/api-docs/**",
	                "/swagger-custom-ui.html",
	                "/actuator/prometheus",
	                "/actuator/health"
	            ).permitAll()
	            .anyRequest().authenticated()
	        )
	        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
	        .addFilterBefore(tokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

	    return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOrigin("*");
		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(false);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(JwtAuthenticationProvider jwtAuthenticationProvider) {
		return new ProviderManager(jwtAuthenticationProvider);
	}

	@Bean
	public JwtAuthenticationProvider jwtAuthenticationProvider(
		PasswordEncoder passwordEncoder,
		FindUserUseCase findUserUseCase
	) {
		return new JwtAuthenticationProvider(passwordEncoder, findUserUseCase);
	}

	@Bean
	public TokenAuthenticationFilter tokenAuthenticationFilter(TokenProvider tokenProvider) {
		return new TokenAuthenticationFilter(tokenProvider);
	}
}
