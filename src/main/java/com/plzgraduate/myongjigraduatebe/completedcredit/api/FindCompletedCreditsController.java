package com.plzgraduate.myongjigraduatebe.completedcredit.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plzgraduate.myongjigraduatebe.completedcredit.api.dto.CompletedCreditResponse;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.FindCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;

import lombok.RequiredArgsConstructor;

@WebAdapter
@RequestMapping(value = "/api/v1/graduations/credits")
@RequiredArgsConstructor
public class FindCompletedCreditsController implements FindCompletedCreditApiPresentation{

	private final FindCompletedCreditUseCase findCompletedCreditUseCase;

	@GetMapping()
	public List<CompletedCreditResponse> getCompletedCredits(@LoginUser Long userId) {
		return findCompletedCreditUseCase.findCompletedCredits(userId).stream()
			.map(CompletedCreditResponse::from)
			.collect(Collectors.toList());
	}
}
