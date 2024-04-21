package com.plzgraduate.myongjigraduatebe.graduation.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

@Component
public class SingleCalculateDetailGraduationUseCaseResolver implements CalculateDetailGraduationUseCaseResolver {

	private List<CalculateDetailGraduationUseCase> calculateDetailGraduationUseCases;

	private final ApplicationContext applicationContext;

	@Autowired
	public SingleCalculateDetailGraduationUseCaseResolver(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		initCalculateDetailGraduationUseCase();
	}

	@Override
	public CalculateDetailGraduationUseCase resolveCalculateDetailGraduationUseCase(
		GraduationCategory graduationCategory) {
		return calculateDetailGraduationUseCases.stream()
			.filter(calculateDetailGraduationUseCase -> calculateDetailGraduationUseCase.supports(graduationCategory))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("No calculate detail graduation case found"));
	}

	private void initCalculateDetailGraduationUseCase() {
		Map<String, CalculateDetailGraduationUseCase> matchingBeans = applicationContext.getBeansOfType(
			CalculateDetailGraduationUseCase.class);
		this.calculateDetailGraduationUseCases = new ArrayList<>(matchingBeans.values());
	}
}
