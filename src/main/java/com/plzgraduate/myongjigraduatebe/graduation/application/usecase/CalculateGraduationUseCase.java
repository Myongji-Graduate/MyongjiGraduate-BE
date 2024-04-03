package com.plzgraduate.myongjigraduatebe.graduation.application.usecase;

import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface CalculateGraduationUseCase {

	GraduationResult calculateGraduation(User user, Set<TakenLecture> takenLectures);
}
