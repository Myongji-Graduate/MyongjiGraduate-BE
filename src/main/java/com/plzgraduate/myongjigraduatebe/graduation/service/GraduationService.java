package com.plzgraduate.myongjigraduatebe.graduation.service;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationResult;

public interface GraduationService {
  GraduationResult getResult(AuthenticatedUser user);
}
