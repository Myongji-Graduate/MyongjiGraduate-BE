package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.FusionMajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.FusionMajorPolicyJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.FusionMajorLectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.FusionMajorPolicyRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FusionMajorMembershipPersistenceAdapterTest {

	@Mock
	private FusionMajorLectureRepository lectureRepository;
	@Mock
	private FusionMajorPolicyRepository policyRepository;
	@Mock
	private LectureMapper lectureMapper;
	@Mock
	private FusionMajorLectureJpaEntity membership;
	@Mock
	private FusionMajorPolicyJpaEntity policy;

	private FusionMajorMembershipPersistenceAdapter adapter;
	private LectureJpaEntity lectureEntity;
	private Lecture lecture;

	@BeforeEach
	void setUp() {
		adapter = new FusionMajorMembershipPersistenceAdapter(
			lectureRepository, policyRepository, lectureMapper);
		lectureEntity = LectureJpaEntity.builder().id("HED00201").name("융합R통계분석").credit(3).build();
		lecture = Lecture.from("HED00201", "융합R통계분석", 3);
	}

	@Test
	void returnsEmptyValuesForMissingFusionMajor() {
		assertThat(adapter.findFusionMajorLectureIds(null)).isEmpty();
		assertThat(adapter.findFusionMajorLectureIds(List.of())).isEmpty();
		assertThat(adapter.findLectures(null, 22)).isEmpty();
		assertThat(adapter.findLectures(" ", 22, "MAJOR")).isEmpty();
		assertThat(adapter.findMandatoryLectureIds(null, 22, "MAJOR")).isEmpty();
		assertThat(adapter.findRequiredCredit("", 22)).isZero();
		assertThat(adapter.findRequiredCredit(null, 22, "경영학과")).isZero();
		assertThat(adapter.findRequiredBasicCredit(" ", 22, "경영학과")).isZero();
		assertThat(adapter.findRequiredPrimaryMajorCredit(null, 22, "경영학과")).isZero();
		assertThat(adapter.findEquivalenceKeys("", 22)).isEmpty();
	}

	@Test
	void findsAndNormalizesFusionMajorLecturesAndMappings() {
		given(lectureRepository.findLectureIdsIn(List.of("HED00201")))
			.willReturn(List.of("HED00201"));
		given(lectureRepository.findApplicable("AI엔터프라이즈솔루션", 22))
			.willReturn(List.of(membership));
		given(lectureRepository.findApplicableByArea("휴먼복지행정", 22, "MAJOR"))
			.willReturn(List.of(membership));
		given(membership.getLecture()).willReturn(lectureEntity);
		given(membership.getMandatory()).willReturn(1);
		given(membership.getEquivalenceKey()).willReturn("R_STAT");
		given(lectureMapper.mapToLectureModel(lectureEntity)).willReturn(lecture);

		assertThat(adapter.findFusionMajorLectureIds(List.of("HED00201")))
			.containsExactly("HED00201");
		assertThat(adapter.findLectures("프로세스자동화경영전공", 22)).containsExactly(lecture);
		assertThat(adapter.findLectures("사회복지학전공", 22, "MAJOR")).containsExactly(lecture);
		assertThat(adapter.findMandatoryLectureIds("사회복지학전공", 22, "MAJOR"))
			.containsExactly("HED00201");
		assertThat(adapter.findEquivalenceKeys("프로세스자동화경영전공", 22))
			.isEqualTo(Map.of("HED00201", "R_STAT"));
	}

	@Test
	void selectsBusinessOverridesAndFallsBackToDefaultCredits() {
		given(policyRepository
			.findFirstByFusionMajorAndStartEntryYearLessThanEqualAndEndEntryYearGreaterThanEqual(
				"AI엔터프라이즈솔루션", 22, 22))
			.willReturn(Optional.of(policy));
		given(policy.getRequiredCredit()).willReturn(36);
		given(policy.getBusinessRequiredCredit()).willReturn(30);
		given(policy.getBasicCredit()).willReturn(3);
		given(policy.getBusinessBasicCredit()).willReturn(6);
		given(policy.getPrimaryMajorCredit()).willReturn(48);
		given(policy.getBusinessPrimaryMajorCredit()).willReturn(42);

		assertThat(adapter.findRequiredCredit("AI엔터프라이즈솔루션전공", 22)).isEqualTo(36);
		assertThat(adapter.findRequiredCredit(
			"AI엔터프라이즈솔루션전공", 22, "경영학전공")).isEqualTo(30);
		assertThat(adapter.findRequiredBasicCredit(
			"AI엔터프라이즈솔루션전공", 22, "경영학전공")).isEqualTo(6);
		assertThat(adapter.findRequiredPrimaryMajorCredit(
			"AI엔터프라이즈솔루션전공", 22, "경영학전공")).isEqualTo(42);
		assertThat(adapter.findRequiredCredit(
			"AI엔터프라이즈솔루션전공", 22, "알수없는전공")).isEqualTo(36);
		assertThat(adapter.findRequiredBasicCredit(
			"AI엔터프라이즈솔루션전공", 22, null)).isEqualTo(3);
		assertThat(adapter.findRequiredPrimaryMajorCredit(
			"AI엔터프라이즈솔루션전공", 22, "국어국문학과")).isEqualTo(48);
	}

	@Test
	void returnsDefaultsWhenPolicyDoesNotExist() {
		given(policyRepository
			.findFirstByFusionMajorAndStartEntryYearLessThanEqualAndEndEntryYearGreaterThanEqual(
				"글로벌문화", 21, 21))
			.willReturn(Optional.empty());

		assertThat(adapter.findRequiredCredit("글로벌문화전공", 21)).isZero();
		assertThat(adapter.findRequiredCredit("글로벌문화전공", 21, "국어국문학과")).isZero();
		assertThat(adapter.findRequiredBasicCredit("글로벌문화전공", 21, "국어국문학과")).isZero();
		assertThat(adapter.findRequiredPrimaryMajorCredit("글로벌문화전공", 21, "국어국문학과")).isZero();
	}
}
