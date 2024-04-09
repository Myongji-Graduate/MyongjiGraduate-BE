package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CalculateCommonCultureGraduationServiceTest {

	@Mock
	private FindCommonCulturePort findCommonCulturePort;
	@InjectMocks
	private CalculateCommonCultureGraduationService calculateCommonCultureGraduationService;

	@DisplayName("유저의 공통교양 상세 졸업결과를 계산한다.")
	@Test
	void calculateCommonCulture() {
		//given
		User user = User.builder()
			.id(1L).build();
		HashSet<CommonCulture> graduationCommonCultures = new HashSet<>(
			Set.of(CommonCulture.of(Lecture.from("KMA00101"), CHRISTIAN_B)));
		given(findCommonCulturePort.findCommonCulture(user)).willReturn(graduationCommonCultures);

		HashSet<TakenLecture> takenLectures = new HashSet<>(
			Set.of(TakenLecture.builder().lecture(Lecture.from("KMA00101")).build()));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);

		//when
		DetailGraduationResult detailCommonCultureGraduationResult = calculateCommonCultureGraduationService.calculateCommonCulture(
			user, takenLectureInventory, 2);

		//then
		assertThat(detailCommonCultureGraduationResult)
			.extracting("graduationCategory", "isCompleted", "totalCredit", "takenCredit")
			.contains(COMMON_CULTURE, true, 2, 2);
	}

}
