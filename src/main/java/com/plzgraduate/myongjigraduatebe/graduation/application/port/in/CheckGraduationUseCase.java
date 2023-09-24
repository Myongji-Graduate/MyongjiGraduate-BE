package com.plzgraduate.myongjigraduatebe.graduation.application.port.in;

import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface CheckGraduationUseCase {

	GraduationResponse checkGraduation(User user);
}
