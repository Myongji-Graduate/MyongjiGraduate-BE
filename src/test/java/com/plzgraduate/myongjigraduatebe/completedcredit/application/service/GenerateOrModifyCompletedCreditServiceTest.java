package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.GenerateOrModifyCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.FreeElectiveGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.NormalCultureGraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class GenerateOrModifyCompletedCreditServiceTest {

	@Mock
	private FindCompletedCreditPort findCompletedCreditPort;
	@Mock
	private GenerateOrModifyCompletedCreditPort generateOrModifyCompletedCreditPort;

	@InjectMocks
	private GenerateOrModifyCompletedCreditService generateOrModifyCompletedCreditService;

	@DisplayName("새로운 기이수 학점을 저장한다.")
	@Test
	void generateOrModifyCompletedCreditPort() {
		//given
		User user = User.builder()
			.id(1L).build();
		given(findCompletedCreditPort.findCompletedCredit(user)).willReturn(List.of());

		int eachDetailGraduationResultTotalCredit = 10;
		int eachDetailGraduationResultTakenCredit = 5;

		List<DetailGraduationResult> detailGraduationResults = createDetailGraduationResults(
			eachDetailGraduationResultTotalCredit, eachDetailGraduationResultTakenCredit);

		GraduationResult graduationResult = GraduationResult.builder()
			.detailGraduationResults(detailGraduationResults)
			.chapelResult(
				ChapelResult.builder()
					.takenCount(3)
					.build())
			.normalCultureGraduationResult(
				NormalCultureGraduationResult.builder()
					.totalCredit(eachDetailGraduationResultTotalCredit)
					.takenCredit(eachDetailGraduationResultTakenCredit)
					.build())
			.freeElectiveGraduationResult(
				FreeElectiveGraduationResult.builder()
					.totalCredit(eachDetailGraduationResultTotalCredit)
					.takenCredit(eachDetailGraduationResultTakenCredit)
					.build())
			.build();
		ArgumentCaptor<List<CompletedCredit>> completedCreditArgumentCaptor = ArgumentCaptor.forClass(List.class);
		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

		//when
		generateOrModifyCompletedCreditService.generateOrModifyCompletedCredit(user, graduationResult);

		//then
		then(generateOrModifyCompletedCreditPort).should()
			.generateOrModifyCompletedCredits(userArgumentCaptor.capture(), completedCreditArgumentCaptor.capture());
		List<CompletedCredit> savedCompletedCredits = completedCreditArgumentCaptor.getValue();
		Assertions.assertThat(savedCompletedCredits).hasSize(detailGraduationResults.size() + 3)
			.extracting("graduationCategory", "totalCredit", "takenCredit")
			.containsOnly(
				tuple(GraduationCategory.COMMON_CULTURE, eachDetailGraduationResultTotalCredit,
					(double)eachDetailGraduationResultTakenCredit),
				tuple(GraduationCategory.CORE_CULTURE, eachDetailGraduationResultTotalCredit,
					(double)eachDetailGraduationResultTakenCredit),
				tuple(GraduationCategory.PRIMARY_MAJOR, eachDetailGraduationResultTotalCredit,
					(double)eachDetailGraduationResultTakenCredit),
				tuple(GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE, eachDetailGraduationResultTotalCredit,
					(double)eachDetailGraduationResultTakenCredit),
				tuple(GraduationCategory.CHAPEL, 4, 1.5),
				tuple(GraduationCategory.NORMAL_CULTURE, eachDetailGraduationResultTotalCredit,
					(double)eachDetailGraduationResultTakenCredit),
				tuple(GraduationCategory.FREE_ELECTIVE, eachDetailGraduationResultTotalCredit,
					(double)eachDetailGraduationResultTakenCredit));

	}
	//TODO: update 테스트 케이스 추가

	private List<DetailGraduationResult> createDetailGraduationResults(int totalCredit, int takenCredit) {
		return List.of(DetailGraduationResult.builder()
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(totalCredit)
				.takenCredit(takenCredit).build(),
			DetailGraduationResult.builder()
				.graduationCategory(GraduationCategory.CORE_CULTURE)
				.totalCredit(totalCredit)
				.takenCredit(takenCredit)
				.build(),
			DetailGraduationResult.builder()
				.graduationCategory(GraduationCategory.PRIMARY_MAJOR)
				.totalCredit(totalCredit)
				.takenCredit(takenCredit)
				.build(),
			DetailGraduationResult.builder()
				.graduationCategory(GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE)
				.totalCredit(totalCredit)
				.takenCredit(takenCredit)
				.build());
	}
}
