package com.plzgraduate.myongjigraduatebe.graduation.application.port.out;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface LoadGraduationRequirementPort {

	GraduationRequirement loadGraduationRequirement(User user);
}
