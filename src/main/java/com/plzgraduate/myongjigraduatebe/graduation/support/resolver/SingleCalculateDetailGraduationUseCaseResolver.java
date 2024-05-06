package com.plzgraduate.myongjigraduatebe.graduation.support.resolver;

import java.util.List;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

import lombok.RequiredArgsConstructor;

@Component()
@RequiredArgsConstructor
public class SingleCalculateDetailGraduationUseCaseResolver implements CalculateDetailGraduationUseCaseResolver {

	private final List<CalculateDetailGraduationUseCase> calculateDetailGraduationUseCases;

	@Override
	public CalculateDetailGraduationUseCase resolveCalculateDetailGraduationUseCase(
		GraduationCategory graduationCategory) {
		return calculateDetailGraduationUseCases.stream()
			.filter(calculateDetailGraduationUseCase -> calculateDetailGraduationUseCase.supports(graduationCategory))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("No calculate detail graduation case found"));
	}

}
