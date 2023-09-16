package com.plzgraduate.myongjigraduatebe.graduation.application.port;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.*;
import static com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester.*;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response.GraduationResponse;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.out.FindGraduationRequirementPort;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindBasicAcademicalCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindCommonCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindCoreCulturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.out.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CheckGraduationServiceTest {

	Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@Mock
	private FindGraduationRequirementPort findGraduationRequirementPort;
	@Mock
	private FindTakenLecturePort findTakenLecturePort;
	@Mock
	private FindCommonCulturePort findCommonCulturePort;
	@Mock
	private FindCoreCulturePort findCoreCulturePort;
	@Mock
	private FindBasicAcademicalCulturePort findBasicAcademicalCulturePort;
	@Mock
	private FindMajorPort findMajorPort;
	@InjectMocks
	private CheckGraduationService checkGraduationService;

	@DisplayName("유저 정보, 유저의 수강 과목 정보로 졸업 결과를 확인한다.")
	@Test
	void checkGraduation() {
		//given
		User user = UserFixture.응용소프트웨어학과_19학번_영어_면제();
		given(findGraduationRequirementPort.findGraduationRequirement(user))
			.willReturn(GraduationRequirement.builder()
				.commonCultureCredit(9)
				.coreCultureCredit(12)
				.basicAcademicalCredit(3)
				.majorCredit(3)
				.normalCultureCredit(9)
				.freeElectiveCredit(3)
				.totalCredit(41).build());
		given(findTakenLecturePort.findTakenLectureSetByUser(user))
			.willReturn(new HashSet<>(Set.of(
				TakenLecture.of(user, mockLectureMap.get("KMA00101"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02102"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02104"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02106"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02107"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02108"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02109"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02137"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02112"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02114"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02128"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02135"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMB02123"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("HED01206"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2019, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2019, SECOND),
				TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2020, FIRST),
				TakenLecture.of(user, mockLectureMap.get("KMA02101"), 2020, SECOND),
				TakenLecture.of(user, mockLectureMap.get("KMA02113"), 2020, SECOND),
				TakenLecture.of(user, mockLectureMap.get("KMA02130"), 2020, SECOND)
			)));
		given(findCommonCulturePort.findCommonCulture(user))
			.willReturn(new HashSet<>(Set.of(
				CommonCulture.of(mockLectureMap.get("KMA00101"), CHRISTIAN_A),
				CommonCulture.of(mockLectureMap.get("KMA02102"), CHRISTIAN_A),
				CommonCulture.of(mockLectureMap.get("KMA02104"), EXPRESSION),
				CommonCulture.of(mockLectureMap.get("KMA02106"), ENGLISH),
				CommonCulture.of(mockLectureMap.get("KMA02107"), ENGLISH),
				CommonCulture.of(mockLectureMap.get("KMA02108"), ENGLISH),
				CommonCulture.of(mockLectureMap.get("KMA02109"), ENGLISH),
				CommonCulture.of(mockLectureMap.get("KMA02137"), CAREER))));
		given(findCoreCulturePort.findCoreCulture(user))
			.willReturn(new HashSet<>(Set.of(
				CoreCulture.of(mockLectureMap.get("KMA02112"), HISTORY_PHILOSOPHY),
				CoreCulture.of(mockLectureMap.get("KMA02114"), SOCIETY_COMMUNITY),
				CoreCulture.of(mockLectureMap.get("KMA02128"), CULTURE_ART),
				CoreCulture.of(mockLectureMap.get("KMA02135"), SCIENCE_TECHNOLOGY))));
		given(findBasicAcademicalCulturePort.findBasicAcademicalCulture(user))
			.willReturn(new HashSet<>(
				Set.of(BasicAcademicalCulture.of(mockLectureMap.get("KMB02123"), ICT.getText()))));
		given((findMajorPort.findMajor(user)))
			.willReturn(new HashSet<>(Set.of(MajorLecture.of(mockLectureMap.get("HED01206"), "응용소프트웨어", 1, 18, 20))));

		//when
		GraduationResponse graduationResponse = checkGraduationService.checkGraduation(user);

		//then
		assertThat(graduationResponse.isGraduated()).isTrue();
	}
}
