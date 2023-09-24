package com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.in.CheckGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping("/api/v1/graduation")
@RequiredArgsConstructor
class GraduationController {

	private final CheckGraduationUseCase checkGraduationUseCase;

	@GetMapping("/result")
	public GraduationResponse check(@LoginUser User user) {
		return checkGraduationUseCase.checkGraduation(user);
	}
}
