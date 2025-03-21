package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.DUAL_MAJOR;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.NORMAL;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory.SUB_MAJOR;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateSingleDetailGraduationServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private FindTakenLectureUseCase findTakenLectureUseCase;
	@Mock
	private List<CalculateDetailGraduationUseCase> calculateDetailGraduationUseCases;

	@InjectMocks
	private CalculateSingleDetailGraduationService calculateSingleDetailGraduationService;

	@DisplayName("이수구분 NORMAL, CHANGE: 단일 카테고리 졸업상세결과를 조회한다.")
	@ValueSource(strings =
		{"COMMON_CULTURE", "CORE_CULTURE", "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR",
			"PRIMARY_BASIC_ACADEMICAL_CULTURE", "NORMAL_CULTURE", "FREE_ELECTIVE"
		})
	@ParameterizedTest
	void calculateSingleDetailGraduationForNormalAndChange(String graduationCategoryName) {
		// given
		User user = User.builder()
			.id(1L)
			.entryYear(19)
			.studentCategory(NORMAL)
			.primaryMajor("응용소프트웨어전공")
			.build();
		GraduationCategory graduationCategory = GraduationCategory.valueOf(graduationCategoryName);
		CalculateDetailGraduationUseCase calculateDetailGraduationUseCase = mock(
			CalculateDetailGraduationUseCase.class);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());

		given(findUserUseCase.findUserById(user.getId())).willReturn(user);
		given(findTakenLectureUseCase.findTakenLectures(user.getId())).willReturn(
			takenLectureInventory);
		given(calculateDetailGraduationUseCases.stream()).willReturn(
			Stream.of(calculateDetailGraduationUseCase));
		given(calculateDetailGraduationUseCase.supports(graduationCategory)).willReturn(true);
		given(calculateDetailGraduationUseCase.calculateSingleDetailGraduation(
			any(User.class), any(GraduationCategory.class),
			any(TakenLectureInventory.class), any(GraduationRequirement.class)))
			.willReturn(DetailGraduationResult.create(graduationCategory, 10, List.of()));

		// when
		DetailGraduationResult result =
			calculateSingleDetailGraduationService.calculateSingleDetailGraduation(1L,
				graduationCategory);

		// then
		assertNotNull(result);
		then(calculateDetailGraduationUseCase).should()
			.calculateSingleDetailGraduation(any(User.class), any(GraduationCategory.class),
				any(TakenLectureInventory.class), any(GraduationRequirement.class));

	}

	@DisplayName("이수구분 DUAL: 단일 카테고리 졸업상세결과를 조회한다.")
	@ValueSource(strings =
		{"COMMON_CULTURE", "CORE_CULTURE", "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR",
			"DUAL_MANDATORY_MAJOR",
			"DUAL_ELECTIVE_MAJOR", "PRIMARY_BASIC_ACADEMICAL_CULTURE",
			"DUAL_BASIC_ACADEMICAL_CULTURE",
			"NORMAL_CULTURE", "FREE_ELECTIVE"
		})
	@ParameterizedTest
	void calculateSingleDetailGraduationForDual(String graduationCategoryName) {
		// given
		User user = User.builder()
			.id(1L)
			.entryYear(19)
			.studentCategory(DUAL_MAJOR)
			.primaryMajor("응용소프트웨어전공")
			.dualMajor("데이터사이언스전공")
			.build();
		GraduationCategory graduationCategory = GraduationCategory.valueOf(graduationCategoryName);
		CalculateDetailGraduationUseCase calculateDetailGraduationUseCase = mock(
			CalculateDetailGraduationUseCase.class);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());

		given(findUserUseCase.findUserById(user.getId())).willReturn(user);
		given(findTakenLectureUseCase.findTakenLectures(user.getId())).willReturn(
			takenLectureInventory);
		given(calculateDetailGraduationUseCases.stream()).willReturn(
			Stream.of(calculateDetailGraduationUseCase));
		given(calculateDetailGraduationUseCase.supports(graduationCategory)).willReturn(true);
		given(calculateDetailGraduationUseCase.calculateSingleDetailGraduation(
			any(User.class), any(GraduationCategory.class),
			any(TakenLectureInventory.class), any(GraduationRequirement.class)))
			.willReturn(DetailGraduationResult.create(graduationCategory, 10, List.of()));

		// when
		DetailGraduationResult result =
			calculateSingleDetailGraduationService.calculateSingleDetailGraduation(1L,
				graduationCategory);

		// then
		assertNotNull(result);
		then(calculateDetailGraduationUseCase).should()
			.calculateSingleDetailGraduation(any(User.class), any(GraduationCategory.class),
				any(TakenLectureInventory.class), any(GraduationRequirement.class));

	}

	@DisplayName("이수구분 SUB: 단일 카테고리 졸업상세결과를 조회한다.")
	@ValueSource(strings =
		{"COMMON_CULTURE", "CORE_CULTURE", "PRIMARY_MANDATORY_MAJOR", "PRIMARY_ELECTIVE_MAJOR",
			"SUB_MAJOR",
			"PRIMARY_BASIC_ACADEMICAL_CULTURE", "NORMAL_CULTURE", "FREE_ELECTIVE"
		})
	@ParameterizedTest
	void calculateSingleDetailGraduationForSub(String graduationCategoryName) {
		// given
		User user = User.builder()
			.id(1L)
			.entryYear(19)
			.studentCategory(SUB_MAJOR)
			.primaryMajor("응용소프트웨어전공")
			.build();
		GraduationCategory graduationCategory = GraduationCategory.valueOf(graduationCategoryName);
		CalculateDetailGraduationUseCase calculateDetailGraduationUseCase = mock(
			CalculateDetailGraduationUseCase.class);
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(new HashSet<>());

		given(findUserUseCase.findUserById(user.getId())).willReturn(user);
		given(findTakenLectureUseCase.findTakenLectures(user.getId())).willReturn(
			takenLectureInventory);
		given(calculateDetailGraduationUseCases.stream()).willReturn(
			Stream.of(calculateDetailGraduationUseCase));
		given(calculateDetailGraduationUseCase.supports(graduationCategory)).willReturn(true);
		given(calculateDetailGraduationUseCase.calculateSingleDetailGraduation(
			any(User.class), any(GraduationCategory.class),
			any(TakenLectureInventory.class), any(GraduationRequirement.class)))
			.willReturn(DetailGraduationResult.create(graduationCategory, 10, List.of()));

		// when
		DetailGraduationResult result =
			calculateSingleDetailGraduationService.calculateSingleDetailGraduation(1L,
				graduationCategory);

		// then
		assertNotNull(result);
		then(calculateDetailGraduationUseCase).should()
			.calculateSingleDetailGraduation(any(User.class), any(GraduationCategory.class),
				any(TakenLectureInventory.class), any(GraduationRequirement.class));

	}

}
