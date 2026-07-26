package com.plzgraduate.myongjigraduatebe.graduation.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FusionMajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateFusionMajorGraduationServiceTest {

	@Mock
	private FusionMajorMembershipPort membershipPort;

	@InjectMocks
	private CalculateFusionMajorGraduationService service;

	@Test
	void calculatesBasicAndMajorRequirementsAndDeduplicatesEquivalentLectures() {
		User user = User.builder()
			.primaryMajor("경영학과")
			.associatedMajor("AI엔터프라이즈솔루션전공")
			.entryYear(22)
			.build();
		Lecture basic = Lecture.from("BASIC-1", "융합기초", 3);
		Lecture oldMajor = Lecture.from("MAJOR-OLD", "구과목", 3);
		Lecture newMajor = Lecture.from("MAJOR-NEW", "신과목", 3);
		Lecture mandatory = Lecture.from("MAJOR-MANDATORY", "필수과목", 3);
		TakenLecture takenBasic = TakenLecture.custom(user, basic);
		TakenLecture takenOld = TakenLecture.custom(user, oldMajor);
		TakenLecture takenNew = TakenLecture.custom(user, newMajor);
		TakenLecture takenMandatory = TakenLecture.custom(user, mandatory);
		TakenLectureInventory inventory = TakenLectureInventory.from(
			Set.of(takenBasic, takenOld, takenNew, takenMandatory));

		given(membershipPort.findRequiredBasicCredit(
			"AI엔터프라이즈솔루션전공", 22, "경영학과")).willReturn(3);
		given(membershipPort.findRequiredCredit(
			"AI엔터프라이즈솔루션전공", 22, "경영학과")).willReturn(6);
		given(membershipPort.findLectures(
			"AI엔터프라이즈솔루션전공", 22, "BASIC"))
			.willReturn(new HashSet<>(Set.of(basic)));
		given(membershipPort.findLectures(
			"AI엔터프라이즈솔루션전공", 22, "MAJOR"))
			.willReturn(new HashSet<>(Set.of(oldMajor, newMajor, mandatory)));
		given(membershipPort.findEquivalenceKeys(
			"AI엔터프라이즈솔루션전공", 22))
			.willReturn(Map.of("MAJOR-OLD", "SAME", "MAJOR-NEW", "SAME"));
		given(membershipPort.findMandatoryLectureIds(
			"AI엔터프라이즈솔루션전공", 22, "BASIC")).willReturn(Set.of());
		given(membershipPort.findMandatoryLectureIds(
			"AI엔터프라이즈솔루션전공", 22, "MAJOR")).willReturn(Set.of("MAJOR-MANDATORY"));

		List<DetailGraduationResult> results = service.calculate(user, inventory);

		assertThat(results).extracting(DetailGraduationResult::getGraduationCategory)
			.containsExactly(
				GraduationCategory.FUSION_BASIC_ACADEMICAL_CULTURE,
				GraduationCategory.FUSION_MAJOR);
		assertThat(results).extracting(DetailGraduationResult::isCompleted)
			.containsExactly(true, true);
		assertThat(results).extracting(DetailGraduationResult::getTakenCredit)
			.containsExactly(3.0, 6.0);
		assertThat(inventory.getTakenLectures()).isEmpty();
	}

	@Test
	void marksAreaIncompleteWhenMandatoryLectureIsMissing() {
		User user = User.builder()
			.primaryMajor("국어국문학과")
			.associatedMajor("글로벌문화전공")
			.entryYear(21)
			.build();
		Lecture elective = Lecture.from("ELECTIVE", "선택", 3);
		TakenLectureInventory inventory = TakenLectureInventory.from(
			Set.of(TakenLecture.custom(user, elective)));

		given(membershipPort.findRequiredBasicCredit("글로벌문화전공", 21, "국어국문학과"))
			.willReturn(0);
		given(membershipPort.findRequiredCredit("글로벌문화전공", 21, "국어국문학과"))
			.willReturn(3);
		given(membershipPort.findLectures("글로벌문화전공", 21, "BASIC"))
			.willReturn(new HashSet<>());
		given(membershipPort.findLectures("글로벌문화전공", 21, "MAJOR"))
			.willReturn(new HashSet<>(Set.of(elective)));
		given(membershipPort.findEquivalenceKeys("글로벌문화전공", 21)).willReturn(Map.of());
		given(membershipPort.findMandatoryLectureIds("글로벌문화전공", 21, "BASIC"))
			.willReturn(Set.of());
		given(membershipPort.findMandatoryLectureIds("글로벌문화전공", 21, "MAJOR"))
			.willReturn(Set.of("MANDATORY"));

		List<DetailGraduationResult> results = service.calculate(user, inventory);

		assertThat(results.get(1).isCompleted()).isFalse();
		assertThat(results.get(1).getTakenCredit()).isEqualTo(3);
	}
}
