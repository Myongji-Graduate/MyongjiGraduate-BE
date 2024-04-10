package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static org.mockito.BDDMockito.given;

import java.util.HashSet;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.graduation.api.DetailGraduationResolver;
import com.plzgraduate.myongjigraduatebe.graduation.application.dto.ResolvedDetailGraduation;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.FindCommonCulturePersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CalculateSingleDetailGraduationServiceTest {

	@Mock
	private FindUserUseCase findUserUseCase;
	@Mock
	private DetailGraduationResolver detailGraduationResolver;
	@Mock
	private FindTakenLectureUseCase findTakenLectureUseCase;
	@Mock
	private CommonCultureRepository commonCultureRepository;

	@InjectMocks
	private CalculateSingleDetailGraduationService calculateSingleDetailGraduationService;

	@DisplayName("단일 카테고리 졸업상세결과를 조회한다.")
	@Test
	void calculateSingleDetailGraduation() {
		// given
		User user = User.builder()
			.id(1L)
			.entryYear(19)
			.primaryMajor("응용소프트웨어전공").build();
		given(findUserUseCase.findUserById(user.getId())).willReturn(user);

		ResolvedDetailGraduation resolvedDetailGraduation = ResolvedDetailGraduation.builder()
			.calculateDetailGraduationUseCase(new CalculateCommonCultureGraduationService(
				new FindCommonCulturePersistenceAdapter(commonCultureRepository, new LectureMapper())))
			.graduationCategoryTotalCredit(17).build();
		given(detailGraduationResolver.resolveDetailGraduationUseCase(user, COMMON_CULTURE))
			.willReturn(resolvedDetailGraduation);

		given(findTakenLectureUseCase.findTakenLectures(user.getId())).willReturn(
			TakenLectureInventory.from(new HashSet<>()));

		// when
		DetailGraduationResult detailGraduationResult = calculateSingleDetailGraduationService.calculateSingleDetailGraduation(
			user.getId(), COMMON_CULTURE);

		// then
		Assertions.assertThat(detailGraduationResult)
			.extracting("graduationCategory", "totalCredit")
			.contains(COMMON_CULTURE, 17);
	}

}
