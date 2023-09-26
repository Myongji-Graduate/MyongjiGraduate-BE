package com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.CalculateGraduationUseCase;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/graduation")
@RequiredArgsConstructor
class CalculateGraduationController {

	private final CalculateGraduationUseCase calculateGraduationUseCase;

	@GetMapping("/result")
	public GraduationResponse calculate(@LoginUser Long userId) {
		return calculateGraduationUseCase.calculateGraduation(userId);
	}
}
