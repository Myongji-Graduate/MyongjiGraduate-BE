package com.plzgraduate.myongjigraduatebe.auth.service;

import com.plzgraduate.myongjigraduatebe.auth.dto.SignInRequest;
import com.plzgraduate.myongjigraduatebe.auth.dto.SignUpRequest;

public interface AuthService {
  void signUp(SignUpRequest userCreateReq);

  String signIn(SignInRequest request);
}
