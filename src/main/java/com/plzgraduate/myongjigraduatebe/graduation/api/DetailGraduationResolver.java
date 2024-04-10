package com.plzgraduate.myongjigraduatebe.graduation.api;

import java.util.Map;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DetailGraduationResolver {

	Map<CalculateDetailGraduationUseCase, Integer> resolveDetailGraduationUseCase(User user, GraduationCategory graduationCategory);


}
