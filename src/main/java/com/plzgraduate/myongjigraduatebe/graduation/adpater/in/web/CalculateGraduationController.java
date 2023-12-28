package com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.CalculateGraduationUseCase;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/graduation")
@RequiredArgsConstructor
@Tag(name = "CalculateGraduation", description = "유저의 졸업 결과를 계산하는 API")
public class CalculateGraduationController {

	private final CalculateGraduationUseCase calculateGraduationUseCase;

	@Parameter(name = "userId", description = "로그인한 유저의 PK값")
	@GetMapping("/result")
	public GraduationResponse calculate(@LoginUser Long userId) {
		return calculateGraduationUseCase.calculateGraduation(userId);
	}
}
