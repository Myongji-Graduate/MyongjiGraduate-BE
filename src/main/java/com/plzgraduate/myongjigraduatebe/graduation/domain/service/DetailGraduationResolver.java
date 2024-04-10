package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import com.plzgraduate.myongjigraduatebe.graduation.application.dto.ResolvedDetailGraduation;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DetailGraduationResolver {

	ResolvedDetailGraduation resolveDetailGraduationUseCase(User user, GraduationCategory graduationCategory);


}
