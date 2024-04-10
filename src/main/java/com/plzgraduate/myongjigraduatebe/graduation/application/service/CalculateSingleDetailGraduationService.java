package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.graduation.api.DetailGraduationResolver;
import com.plzgraduate.myongjigraduatebe.graduation.application.dto.ResolvedDetailGraduation;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalculateSingleDetailGraduationService implements CalculateSingleDetailGraduationUseCase {

	private final FindUserUseCase findUserUseCase;
	private final DetailGraduationResolver detailGraduationResolver;
	private final FindTakenLectureUseCase findTakenLectureUseCase;

	@Override
	public DetailGraduationResult calculateSingleDetailGraduation(Long userId, GraduationCategory graduationCategory) {
		User user = findUserUseCase.findUserById(userId);
		ResolvedDetailGraduation resolvedDetailGraduation = detailGraduationResolver.resolveDetailGraduationUseCase(
			user, graduationCategory);
		TakenLectureInventory takenLectures = findTakenLectureUseCase.findTakenLectures(userId);

		CalculateDetailGraduationUseCase calculateDetailGraduationUseCase = resolvedDetailGraduation.getCalculateDetailGraduationUseCase();
		return calculateDetailGraduationUseCase.calculateDetailGraduation(user, takenLectures,
			resolvedDetailGraduation.getGraduationCategoryTotalCredit());
	}
}
