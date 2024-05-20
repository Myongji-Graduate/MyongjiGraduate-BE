package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.HashSet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.graduation.support.resolver.CalculateDetailGraduationUseCaseResolver;
import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class
CalculateSingleDetailGraduationServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindTakenLectureUseCase findTakenLectureUseCase;
	@Mock
	private CalculateDetailGraduationUseCaseResolver calculateDetailGraduationUseCaseResolver;
	@Mock
	private CalculateDetailGraduationUseCase calculateDetailGraduationUseCase;

	@InjectMocks
	private CalculateSingleDetailGraduationService calculateSingleDetailGraduationService;

	@DisplayName("단일 카테고리 졸업상세결과를 조회한다.")
	@ValueSource(strings =
		{"COMMON_CULTURE", "CORE_CULTURE", "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR", "DUAL_MAJOR",
			"SUB_MAJOR", "PRIMARY_BASIC_ACADEMICAL_CULTURE", "DUAL_BASIC_ACADEMICAL_CULTURE"
		})
	@ParameterizedTest
	void calculateSingleDetailGraduation(String graduationCategoryName) {
		// given
		User user = User.builder()
			.id(1L)
			.entryYear(19)
			.primaryMajor("응용소프트웨어전공").build();
		given(findUserUseCase.findUserById(user.getId())).willReturn(user);

		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());
		given(findTakenLectureUseCase.findTakenLectures(user.getId())).willReturn(
			takenLectureInventory);

		GraduationCategory graduationCategory = GraduationCategory.valueOf(graduationCategoryName);
		given(calculateDetailGraduationUseCaseResolver.resolveCalculateDetailGraduationUseCase(
			graduationCategory)).willReturn(calculateDetailGraduationUseCase);

		// when
		calculateSingleDetailGraduationService.calculateSingleDetailGraduation(
			user.getId(), graduationCategory);

		// then
		then(calculateDetailGraduationUseCase).should()
			.calculateDetailGraduation(any(User.class), any(TakenLectureInventory.class),
				any(GraduationRequirement.class));

	}

}
