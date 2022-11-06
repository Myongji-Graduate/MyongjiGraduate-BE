package com.plzgraduate.myongjigraduatebe.auth.service;

import java.util.Date;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.plzgraduate.myongjigraduatebe.auth.dto.SignInRequest;
import com.plzgraduate.myongjigraduatebe.auth.dto.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.common.config.JwtConfig;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtAuthService implements AuthService {

  public static final String ERROR_MESSAGE_FOR_SIGN_IN = "아이디 혹은 비밀번호를 확인하세요.";
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtConfig jwtConfig;

  @Override
  public void signUp(SignUpRequest signUpReq) {
    User newUser = signUpReq.toEntity(passwordEncoder);

    if (userRepository.existsByUserIdOrStudentNumber(newUser.getUserId(), newUser.getStudentNumber())) {
      throw new IllegalArgumentException("이미 가입된 학번 또는 아이디입니다.");
    }

    userRepository.save(newUser);
  }

  @Override
  public String signIn(SignInRequest request) {
    User user = userRepository
        .findByUserId(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_FOR_SIGN_IN));

    if (!passwordEncoder.matches(request
                                     .getPassword()
                                     .getValue(), user.getPassword())) {
      throw new IllegalArgumentException(ERROR_MESSAGE_FOR_SIGN_IN);
    }

    return createAuthorizationHeader(user);
  }

  private String createAuthorizationHeader(User user) {
    String jwt = createJwt(user);
    return String.format("%s %s", jwtConfig.getPrefix(), jwt);
  }

  private String createJwt(User user) {
    Date now = new Date();
    Date expireAt = new Date(now.getTime() + jwtConfig.getExpirySeconds() * 1000L);
    String[] roles = user
        .getRoles()
        .toArray(new String[0]);

    return JWT
        .create()
        .withIssuer(jwtConfig.getIssuer())
        .withIssuedAt(now)
        .withExpiresAt(expireAt)
        .withClaim("id", user.getId())
        .withArrayClaim("roles", roles)
        .sign(Algorithm.HMAC512(jwtConfig.getClientSecret()));
  }
}
