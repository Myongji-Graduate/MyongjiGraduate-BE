package com.plzgraduate.myongjigraduatebe.graduation.domain.service;

import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class StudentGraduationStrategyFactory {

	private final Map<StudentCategory, StudentGraduationStrategy> strategies;

	public StudentGraduationStrategyFactory(List<StudentGraduationStrategy> strategies) {
		this.strategies = strategies.stream()
			.collect(Collectors.toMap(
				StudentGraduationStrategy::getSupportedStudentCategory,
				Function.identity()
			));
	}

	public StudentGraduationStrategy getStrategy(StudentCategory studentCategory) {
		StudentGraduationStrategy strategy = strategies.get(studentCategory);
		if (strategy == null) {
			// CHANGE_MAJOR는 NORMAL과 동일하게 처리
			if (studentCategory == StudentCategory.CHANGE_MAJOR) {
				strategy = strategies.get(StudentCategory.NORMAL);
			}
			// 기본적으로 NORMAL 전략을 사용하거나, 지원하지 않는 경우 예외를 던집니다
			if (strategy == null) {
				strategy = strategies.get(StudentCategory.NORMAL);
			}
			if (strategy == null) {
				throw new IllegalArgumentException(
					"지원하지 않는 학생 유형입니다: " + studentCategory
				);
			}
		}
		return strategy;
	}
}

