package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult.GRADUATION_COUNT;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.CHAPEL;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.FREE_ELECTIVE;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.NORMAL_CULTURE;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.GenerateOrModifyCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional
@RequiredArgsConstructor
class GenerateOrModifyCompletedCreditService implements GenerateOrModifyCompletedCreditUseCase {

	private final FindCompletedCreditPort findCompletedCreditPort;
	private final GenerateOrModifyCompletedCreditPort generateOrModifyCompletedCreditPort;

	private final CalculateGraduationUseCase calculateGraduationUseCase;

	//TODO()
	@Override
	public void generateOrModifyCompletedCredit(User user) {
		List<CompletedCredit> completedCredits = findCompletedCreditPort.findCompletedCredit(user);
		GraduationResult graduationResult = calculateGraduationUseCase.calculateGraduation(user);
		List<DetailGraduationResult> detailGraduationResults = graduationResult.getDetailGraduationResults();

		List<CompletedCredit> updatedCompletedCredits = createGeneralCompletedCreditModel(
			completedCredits,
			detailGraduationResults
		);
		CompletedCredit chapelCompletedCreditModel = createOrUpdateChapelCompletedCreditModel(
			completedCredits,
			graduationResult,
			user
		);
		CompletedCredit normalCultureCompletedCreditModel = createOrUpdateNormalCultureCompletedCreditModel(
			completedCredits, graduationResult);
		CompletedCredit freeElectiveCompletedCreditModel = createOrUpdateFreeElectiveCompletedCreditModel(
			completedCredits, graduationResult, user, detailGraduationResults);

		ArrayList<CompletedCredit> allCompletedCreditModels = new ArrayList<>(
			updatedCompletedCredits);
		allCompletedCreditModels.addAll(
			List.of(chapelCompletedCreditModel, normalCultureCompletedCreditModel,
				freeElectiveCompletedCreditModel
			));
		generateOrModifyCompletedCreditPort.generateOrModifyCompletedCredits(
			user,
			allCompletedCreditModels
		);
	}

	private List<CompletedCredit> createGeneralCompletedCreditModel(
		List<CompletedCredit> completedCredits,
		List<DetailGraduationResult> detailGraduationResults
	) {
		Map<DetailGraduationResult, Optional<CompletedCredit>> resultMap = detailGraduationResults.stream()
			.collect(Collectors.toMap(
				Function.identity(),
				detailGraduationResult -> completedCredits.stream()
					.filter(completedCredit -> completedCredit.getGraduationCategory()
						.equals(detailGraduationResult.getGraduationCategory()))
					.findFirst()
			));
		return resultMap.keySet()
			.stream()
			.map(detailGraduationResult -> createCompletedCreditModel(
				detailGraduationResult,
				resultMap.get(detailGraduationResult)
			))
			.collect(Collectors.toList());
	}

	private CompletedCredit createCompletedCreditModel(
		DetailGraduationResult detailGraduationResult,
		Optional<CompletedCredit> completedCredit
	) {
		return CompletedCredit.builder()
			.id(completedCredit.map(CompletedCredit::getId)
				.orElse(null))
			.totalCredit(detailGraduationResult.getTotalCredit())
			.takenCredit(detailGraduationResult.getTakenCredit())
			.graduationCategory(detailGraduationResult.getGraduationCategory())
			.build();
	}

	private CompletedCredit createOrUpdateChapelCompletedCreditModel(
			List<CompletedCredit> completedCredits,
			GraduationResult graduationResult,
			User user
	) {
		Optional<CompletedCredit> chapelCompletedCredit = findCompletedCreditByCategory(
				completedCredits, CHAPEL);

		double totalCredit = user.getStudentCategory() == StudentCategory.TRANSFER
				? 0.5
				: GRADUATION_COUNT / 2;

		return chapelCompletedCredit.map(
						completedCredit -> updateCompletedCredit(
								completedCredit,
								totalCredit,
								graduationResult.getChapelResult().getTakenChapelCredit()
						))
				.orElseGet(() -> CompletedCredit.createChapelCompletedCreditModel(
						graduationResult.getChapelResult(), user));
	}


	private CompletedCredit createOrUpdateNormalCultureCompletedCreditModel(
		List<CompletedCredit> completedCredits,
		GraduationResult graduationResult
	) {
		Optional<CompletedCredit> normalCultureCompletedCredit = findCompletedCreditByCategory(
			completedCredits,
			NORMAL_CULTURE
		);
		return normalCultureCompletedCredit.map(
				completedCredit -> updateCompletedCredit(
					completedCredit,
					graduationResult.getNormalCultureGraduationResult()
						.getTotalCredit(),
					graduationResult.getNormalCultureGraduationResult()
						.getTakenCredit()
				))
			.orElseGet(() -> CompletedCredit.createNormalCultureCompletedCreditModel(
				graduationResult.getNormalCultureGraduationResult()));
	}

	private CompletedCredit createOrUpdateFreeElectiveCompletedCreditModel(
		List<CompletedCredit> completedCredits,
		GraduationResult graduationResult,
		User user,
		List<DetailGraduationResult> detailGraduationResults
	) {
		Optional<CompletedCredit> freeElectiveCompletedCredit = findCompletedCreditByCategory(
			completedCredits,
			FREE_ELECTIVE
		);
		int excessCredits = detailGraduationResults.stream()
				.filter(result -> result.getGraduationCategory() == GraduationCategory.FREE_ELECTIVE)
				.mapToDouble(DetailGraduationResult::getTakenCredit)
				.mapToInt(credit -> (int) credit)
				.sum();
		if (user.getStudentCategory() == StudentCategory.TRANSFER) {
			return freeElectiveCompletedCredit.map(
							completedCredit -> updateCompletedCredit(
									completedCredit,
									graduationResult.getFreeElectiveGraduationResult()
											.getTotalCredit(),
									user.getTransferCredit().getFreeElective()+excessCredits
							))
					.orElseGet(() -> CompletedCredit.createFreeElectiveCompletedCreditModel(
							graduationResult.getFreeElectiveGraduationResult()));
		}

		int finalTakenCredit = graduationResult.getFreeElectiveGraduationResult()
				.getTakenCredit();
		return freeElectiveCompletedCredit.map(
				completedCredit -> updateCompletedCredit(
					completedCredit,
					graduationResult.getFreeElectiveGraduationResult()
						.getTotalCredit(),
						finalTakenCredit
				))
			.orElseGet(() -> CompletedCredit.createFreeElectiveCompletedCreditModel(
				graduationResult.getFreeElectiveGraduationResult()));
	}

	private Optional<CompletedCredit> findCompletedCreditByCategory(
		List<CompletedCredit> completedCredits,
		GraduationCategory category
	) {
		return completedCredits.stream()
			.filter(completedCredit -> completedCredit.getGraduationCategory() == category)
			.findFirst();
	}

	private CompletedCredit updateCompletedCredit(
		CompletedCredit completedCredit,
		double totalCredit, double takenCredit
	) {
		completedCredit.updateCredit(totalCredit, takenCredit);
		return completedCredit;
	}
}
