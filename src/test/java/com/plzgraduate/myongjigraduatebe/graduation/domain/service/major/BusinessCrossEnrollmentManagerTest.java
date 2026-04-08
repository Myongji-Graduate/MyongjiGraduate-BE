package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_ELECTIVE_MAJOR;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.PRIMARY_MANDATORY_MAJOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindMajorPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusinessCrossEnrollmentManagerTest {

	@Mock
	private FindMajorPort findMajorPort;

	private BusinessCrossEnrollmentManager manager;

	@BeforeEach
	void setUp() {
		manager = new BusinessCrossEnrollmentManager(findMajorPort);
	}

	// ---------- supportsSingleMajor ----------

	@DisplayName("supportsSingleMajor - NORMAL 학생이 경영 관련 전공이면 true를 반환한다.")
	@Test
	void supportsSingleMajor_normalWithEligibleMajor_returnsTrue() {
		User user = User.builder()
			.primaryMajor("경영학전공")
			.studentCategory(StudentCategory.NORMAL)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsSingleMajor(user)).isTrue();
	}

	@DisplayName("supportsSingleMajor - CHANGE_MAJOR 학생이 국제통상학과이면 true를 반환한다.")
	@Test
	void supportsSingleMajor_changeMajorWithEligibleMajor_returnsTrue() {
		User user = User.builder()
			.primaryMajor("국제통상학과")
			.studentCategory(StudentCategory.CHANGE_MAJOR)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsSingleMajor(user)).isTrue();
	}

	@DisplayName("supportsSingleMajor - DUAL_MAJOR 학생은 false를 반환한다.")
	@Test
	void supportsSingleMajor_dualMajor_returnsFalse() {
		User user = User.builder()
			.primaryMajor("경영학전공")
			.studentCategory(StudentCategory.DUAL_MAJOR)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsSingleMajor(user)).isFalse();
	}

	@DisplayName("supportsSingleMajor - 비경영 전공 학생은 false를 반환한다.")
	@Test
	void supportsSingleMajor_nonEligibleMajor_returnsFalse() {
		User user = User.builder()
			.primaryMajor("응용소프트웨어전공")
			.studentCategory(StudentCategory.NORMAL)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsSingleMajor(user)).isFalse();
	}

	// ---------- supportsDualMajor ----------

	@DisplayName("supportsDualMajor - DUAL_MAJOR이고 양 전공 모두 경영대이면 true를 반환한다.")
	@Test
	void supportsDualMajor_bothBusinessCollege_returnsTrue() {
		// 경영대 소속 전공(16~24학번)
		User user = User.builder()
			.primaryMajor("경영학전공")
			.dualMajor("국제통상학과")
			.entryYear(20)
			.studentCategory(StudentCategory.DUAL_MAJOR)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsDualMajor(user)).isTrue();
	}

	@DisplayName("supportsDualMajor - NORMAL 학생은 false를 반환한다.")
	@Test
	void supportsDualMajor_normalStudent_returnsFalse() {
		User user = User.builder()
			.primaryMajor("경영학전공")
			.dualMajor("국제통상학과")
			.entryYear(20)
			.studentCategory(StudentCategory.NORMAL)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsDualMajor(user)).isFalse();
	}

	@DisplayName("supportsDualMajor - 비경영대 주전공이면 false를 반환한다.")
	@Test
	void supportsDualMajor_nonBusinessPrimary_returnsFalse() {
		User user = User.builder()
			.primaryMajor("응용소프트웨어전공")
			.dualMajor("국제통상학과")
			.entryYear(20)
			.studentCategory(StudentCategory.DUAL_MAJOR)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsDualMajor(user)).isFalse();
	}

	@DisplayName("supportsDualMajor - 알 수 없는 전공(IllegalArgumentException)은 false를 반환한다.")
	@Test
	void supportsDualMajor_unknownMajor_returnsFalse() {
		User user = User.builder()
			.primaryMajor("존재하지않는전공")
			.dualMajor("국제통상학과")
			.entryYear(20)
			.studentCategory(StudentCategory.DUAL_MAJOR)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		assertThat(manager.supportsDualMajor(user)).isFalse();
	}

	// ---------- applySingleMajor ----------

	@DisplayName("applySingleMajor - 이미 전공선택 이수 학점이 충족된 경우 교차인정을 적용하지 않는다.")
	@Test
	void applySingleMajor_noShortfall_doesNothing() {
		// given: totalCredits <= takenCredits → shortfall = 0
		User user = User.builder()
			.primaryMajor("경영학전공")
			.studentCategory(StudentCategory.NORMAL)
			.entryYear(19)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();

		Lecture lecture = Lecture.builder().id("HCA01001").credit(3).build();
		DetailCategoryResult category = DetailCategoryResult.create(PRIMARY_ELECTIVE_MAJOR.getName(), false, 3);
		category.addTakenCredits(3); // 이미 충족

		DetailGraduationResult majorResult = DetailGraduationResult.create(
			PRIMARY_ELECTIVE_MAJOR, 3, List.of(category)
		);

		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.builder().lecture(lecture).build()
		));

		int creditBefore = category.getTakenCredits();

		// when
		manager.applySingleMajor(user, inventory, majorResult);

		// then: 변화 없음
		assertThat(category.getTakenCredits()).isEqualTo(creditBefore);
	}

	@DisplayName("applySingleMajor - 교차인정 가능한 강의가 없으면 학점이 추가되지 않는다.")
	@Test
	void applySingleMajor_noCrossEnrollableLectures_doesNothing() {
		// given: 교차인정 전공 강의 없음
		User user = User.builder()
			.primaryMajor("경영학전공")
			.studentCategory(StudentCategory.NORMAL)
			.entryYear(19)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();

		// findMajor 호출 시 빈 Set 반환
		given(findMajorPort.findMajor("국제통상학과")).willReturn(Set.of());
		given(findMajorPort.findMajor("경영정보학과")).willReturn(Set.of());

		DetailCategoryResult category = DetailCategoryResult.create(PRIMARY_ELECTIVE_MAJOR.getName(), false, 30);
		// takenCredits=0, totalCredits=30 → shortfall=30
		DetailGraduationResult majorResult = DetailGraduationResult.create(
			PRIMARY_ELECTIVE_MAJOR, 30, List.of(category)
		);

		// 인벤토리에 경영학전공 외 강의 없음
		TakenLectureInventory inventory = TakenLectureInventory.from(Set.of(
			TakenLecture.builder().lecture(Lecture.builder().id("HCA01999").credit(3).build()).build()
		));

		// when
		manager.applySingleMajor(user, inventory, majorResult);

		// then: 학점 추가 없음
		assertThat(category.getTakenCredits()).isZero();
	}

	@DisplayName("applySingleMajor - null 인자 전달 시 IllegalArgumentException이 발생한다.")
	@Test
	void applySingleMajor_nullUser_throwsException() {
		TakenLectureInventory emptyInventory = TakenLectureInventory.from(Set.of());
		assertThatThrownBy(() ->
			manager.applySingleMajor(null, emptyInventory, null)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("applySingleMajor - 단일전공 지원 대상이 아닌 사용자는 IllegalArgumentException이 발생한다.")
	@Test
	void applySingleMajor_unsupportedUser_throwsException() {
		User user = User.builder()
			.primaryMajor("응용소프트웨어전공")
			.studentCategory(StudentCategory.NORMAL)
			.entryYear(19)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();
		DetailCategoryResult category = DetailCategoryResult.create(PRIMARY_ELECTIVE_MAJOR.getName(), false, 30);
		DetailGraduationResult majorResult = DetailGraduationResult.create(
			PRIMARY_ELECTIVE_MAJOR, 30, List.of(category)
		);
		TakenLectureInventory emptyInventory = TakenLectureInventory.from(Set.of());

		assertThatThrownBy(() ->
			manager.applySingleMajor(user, emptyInventory, majorResult)
		).isInstanceOf(IllegalArgumentException.class);
	}

	// ---------- restorePrimaryMandatoryForDual ----------

	@DisplayName("restorePrimaryMandatoryForDual - null 인자 전달 시 IllegalArgumentException이 발생한다.")
	@Test
	void restorePrimaryMandatoryForDual_nullUser_throwsException() {
		assertThatThrownBy(() ->
			manager.restorePrimaryMandatoryForDual(null, null, null)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("restorePrimaryMandatoryForDual - 복수전공 지원 대상이 아닌 사용자는 IllegalArgumentException이 발생한다.")
	@Test
	void restorePrimaryMandatoryForDual_unsupportedDualMajor_throwsException() {
		User user = User.builder()
			.primaryMajor("응용소프트웨어전공")
			.dualMajor("국제통상학과")
			.entryYear(20)
			.studentCategory(StudentCategory.DUAL_MAJOR)
			.transferCredit(TransferCredit.empty())
			.exchangeCredit(ExchangeCredit.empty())
			.build();

		DetailCategoryResult mandatoryCategory = DetailCategoryResult.create(
			PRIMARY_MANDATORY_MAJOR.getName(), false, 6
		);
		DetailGraduationResult mandatoryResult = DetailGraduationResult.create(
			PRIMARY_MANDATORY_MAJOR, 6, List.of(mandatoryCategory)
		);

		TakenLectureInventory emptyInventory = TakenLectureInventory.from(Set.of());
		assertThatThrownBy(() ->
			manager.restorePrimaryMandatoryForDual(user, mandatoryResult, emptyInventory)
		).isInstanceOf(IllegalArgumentException.class);
	}

	// ---------- applyDualMajor ----------

	@DisplayName("applyDualMajor - null 인자 전달 시 IllegalArgumentException이 발생한다.")
	@Test
	void applyDualMajor_nullArgument_throwsException() {
		assertThatThrownBy(() ->
			manager.applyDualMajor(null, null, null, null)
		).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("applyDualMajor - 복수전공선택에 9학점 교차인정 후 remainCap이 0이면 주전공선택에는 교차인정하지 않는다.")
	@Test
	void applyDualMajor_remainCapExhausted_doesNotApplyToPrimary() {
		// given: 주전공선택 3학점 부족, 복수전공선택 9학점 부족
		// 주전공선택 이수 강의 3개(9학점) → 복수전공에 9학점 교차 → remainCap=0 → 주전공에는 불가
		Lecture l1 = Lecture.builder().id("P001").credit(3).build();
		Lecture l2 = Lecture.builder().id("P002").credit(3).build();
		Lecture l3 = Lecture.builder().id("P003").credit(3).build();

		DetailCategoryResult primaryMandatoryCategory = DetailCategoryResult.create(
			PRIMARY_MANDATORY_MAJOR.getName(), false, 0);
		DetailGraduationResult primaryMandatoryResult = DetailGraduationResult.create(
			PRIMARY_MANDATORY_MAJOR, 0, List.of(primaryMandatoryCategory));

		DetailCategoryResult primaryElectiveCategory = DetailCategoryResult.create(
			PRIMARY_ELECTIVE_MAJOR.getName(), false, 30);
		primaryElectiveCategory.addRecognizedLectures(List.of(l1, l2, l3)); // 9학점 이수
		DetailGraduationResult primaryElectiveResult = DetailGraduationResult.create(
			PRIMARY_ELECTIVE_MAJOR, 30, List.of(primaryElectiveCategory));

		DetailCategoryResult dualMandatoryCategory = DetailCategoryResult.create(
			com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_MANDATORY_MAJOR.getName(),
			false, 0);
		DetailGraduationResult dualMandatoryResult = DetailGraduationResult.create(
			com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.DUAL_MANDATORY_MAJOR, 0,
			List.of(dualMandatoryCategory));

		DetailCategoryResult dualElectiveCategory = DetailCategoryResult.create(
			DUAL_ELECTIVE_MAJOR.getName(), false, 30);
		// dualElective: takenCredits=21, totalCredits=30 → shortfall=9
		Lecture d1 = Lecture.builder().id("D001").credit(3).build();
		Lecture d2 = Lecture.builder().id("D002").credit(3).build();
		Lecture d3 = Lecture.builder().id("D003").credit(3).build();
		dualElectiveCategory.addRecognizedLectures(List.of(d1, d2, d3));
		DetailGraduationResult dualElectiveResult = DetailGraduationResult.create(
			DUAL_ELECTIVE_MAJOR, 30, List.of(dualElectiveCategory));

		int primaryTakenBefore = primaryElectiveCategory.getTakenCredits();

		// when
		manager.applyDualMajor(
			primaryMandatoryResult,
			primaryElectiveResult,
			dualMandatoryResult,
			dualElectiveResult
		);

		// then: dualElective에 9학점 추가, primaryElective는 변화 없음
		assertThat(dualElectiveCategory.getTakenCredits()).isEqualTo(18);
		assertThat(primaryElectiveCategory.getTakenCredits()).isEqualTo(primaryTakenBefore);
	}
}
