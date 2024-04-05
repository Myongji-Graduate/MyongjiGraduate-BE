package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.GenerateOrModifyCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class GenerateOrModifyCompletedCreditService implements GenerateOrModifyCompletedCreditUseCase {

	private final FindCompletedCreditPort findCompletedCreditPort;
	private final GenerateOrModifyCompletedCreditPort generateOrModifyCompletedCreditPort;

	@Override
	public void generateOrModifyCompletedCredit(User user, GraduationResult graduationResult) {
		List<CompletedCredit> completedCredits = findCompletedCreditPort.findCompletedCredit(user);
		List<DetailGraduationResult> detailGraduationResults = graduationResult.getDetailGraduationResults();

		Map<DetailGraduationResult, Optional<CompletedCredit>> resultMap = detailGraduationResults.stream()
			.collect(Collectors.toMap(
				Function.identity(),
				detailGraduationResult -> completedCredits.stream()
					.filter(cc -> cc.getGraduationCategory().equals(detailGraduationResult.getGraduationCategory()))
					.findFirst()
			));

		List<CompletedCredit> completedCreditModels = resultMap.keySet().stream()
			.map(completedCredit -> createCompletedCreditModel(completedCredit, resultMap.get(completedCredit)))
			.collect(Collectors.toList());

		CompletedCredit chapelCompletedCreditModel = createChapelCompletedCreditModel(completedCredits,
			graduationResult);
		CompletedCredit normalCultureCompletedCreditModel = createNormalCultureCompletedCreditModel(completedCredits,
			graduationResult);
		CompletedCredit freeElectiveCompletedCreditModel = createFreeElectiveCompletedCreditModel(completedCredits,
			graduationResult);

		ArrayList<CompletedCredit> allCompletedCreditModels = new ArrayList<>(completedCreditModels);
		allCompletedCreditModels.addAll(
			List.of(chapelCompletedCreditModel, normalCultureCompletedCreditModel, freeElectiveCompletedCreditModel));
		generateOrModifyCompletedCreditPort.generateOrModifyCompletedCredits(user, allCompletedCreditModels);
	}

	private CompletedCredit createCompletedCreditModel(DetailGraduationResult detailGraduationResult,
		Optional<CompletedCredit> completedCredit) {
		return CompletedCredit.builder()
			.id(completedCredit.map(CompletedCredit::getId).orElse(null))
			.totalCredit(detailGraduationResult.getTotalCredit())
			.takenCredit(detailGraduationResult.getTakenCredit())
			.graduationCategory(detailGraduationResult.getGraduationCategory())
			.build();
	}

	private CompletedCredit createChapelCompletedCreditModel(List<CompletedCredit> completedCredits,
		GraduationResult graduationResult) {
		return completedCredits.stream()
			.filter(completedCredit -> completedCredit.getGraduationCategory() == CHAPEL)
			.map(completedCredit -> CompletedCredit.builder()
				.id(completedCredit.getId())
				.totalCredit(ChapelResult.GRADUATION_COUNT)
				.takenCredit(graduationResult.getChapelResult().getTakenChapelCredit())
				.graduationCategory(CHAPEL).build())
			.findFirst()
			.orElse(CompletedCredit.builder()
				.totalCredit(ChapelResult.GRADUATION_COUNT)
				.takenCredit(graduationResult.getChapelResult().getTakenChapelCredit())
				.graduationCategory(CHAPEL).build());
	}

	private CompletedCredit createNormalCultureCompletedCreditModel(List<CompletedCredit> completedCredits,
		GraduationResult graduationResult) {
		return completedCredits.stream()
			.filter(completedCredit -> completedCredit.getGraduationCategory() == NORMAL_CULTURE)
			.map(completedCredit -> CompletedCredit.builder()
				.id(completedCredit.getId())
				.totalCredit(graduationResult.getNormalCultureGraduationResult().getTotalCredit())
				.takenCredit(graduationResult.getNormalCultureGraduationResult().getTakenCredit())
				.graduationCategory(NORMAL_CULTURE).build())
			.findFirst()
			.orElse(CompletedCredit.builder()
				.totalCredit(graduationResult.getNormalCultureGraduationResult().getTotalCredit())
				.takenCredit(graduationResult.getNormalCultureGraduationResult().getTakenCredit())
				.graduationCategory(NORMAL_CULTURE).build());
	}

	private CompletedCredit createFreeElectiveCompletedCreditModel(List<CompletedCredit> completedCredits,
		GraduationResult graduationResult) {
		return completedCredits.stream()
			.filter(completedCredit -> completedCredit.getGraduationCategory() == FREE_ELECTIVE)
			.map(completedCredit -> CompletedCredit.builder()
				.id(completedCredit.getId())
				.totalCredit(graduationResult.getFreeElectiveGraduationResult().getTotalCredit())
				.takenCredit(graduationResult.getFreeElectiveGraduationResult().getTakenCredit())
				.graduationCategory(FREE_ELECTIVE).build())
			.findFirst()
			.orElse(CompletedCredit.builder()
				.totalCredit(graduationResult.getFreeElectiveGraduationResult().getTotalCredit())
				.takenCredit(graduationResult.getFreeElectiveGraduationResult().getTakenCredit())
				.graduationCategory(FREE_ELECTIVE).build());
	}
}
