package com.plzgraduate.myongjigraduatebe.graduation.application.adpater.out.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.GraduationRequirementRepository;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity.GraduationRequirementJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class GraduationRequirementRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private GraduationRequirementRepository requirementRepository;
	private static final int SUB_MAJOR_CREDIT = 10;


	@BeforeEach
	void setUp() {
		List<GraduationRequirementJpaEntity> graduationRequirementJpaEntityList = new ArrayList<>();
		for (College college : College.values()) {
			graduationRequirementJpaEntityList.addAll(List.of(
				GraduationRequirementJpaEntity.builder()
					.college(college)
					.subMajorCredit(0)
					.startEntryYear(16)
					.endEntryYear(17).build(),

				GraduationRequirementJpaEntity.builder()
					.college(college)
					.subMajorCredit(0)
					.startEntryYear(18)
					.endEntryYear(23).build(),

				GraduationRequirementJpaEntity.builder()
					.college(college)
					.subMajorCredit(SUB_MAJOR_CREDIT)
					.startEntryYear(16)
					.endEntryYear(17).build(),

				GraduationRequirementJpaEntity.builder()
					.college(college)
					.subMajorCredit(SUB_MAJOR_CREDIT)
					.startEntryYear(18)
					.endEntryYear(23).build()));
		}
		requirementRepository.saveAll(graduationRequirementJpaEntityList);
	}

	@DisplayName("유저의 소속 단과대명이 일치하고, 유저의 입학년도가 적용시작 년도, 적용마감 년도 사이에 속하는 단일 전공 졸업 조건을 조회한다.")
	@ParameterizedTest
	@MethodSource("users")
	void findSingleMajorRequirementByUser(User user) {
		//given
		//when
		GraduationRequirementJpaEntity graduationRequirementJpaEntity = requirementRepository.findSingleMajorRequirementByUser(
			College.findBelongingCollege(user.getMajor()),
			user.getEntryYear());

		//then
		Assertions.assertThat(graduationRequirementJpaEntity)
			.extracting("college", "subMajorCredit")
			.contains(College.findBelongingCollege(user.getMajor()), 0);
		Assertions.assertThat(graduationRequirementJpaEntity.getStartEntryYear())
			.isLessThanOrEqualTo(user.getEntryYear());
		Assertions.assertThat(graduationRequirementJpaEntity.getEndEntryYear())
			.isGreaterThanOrEqualTo(user.getEntryYear());
	}

	@DisplayName("유저의 소속 단과대명이 일치하고, 유저의 입학년도가 적용시작 년도, 적용마감 년도 사이에 속하는 단일 전공 졸업 조건을 조회한다.")
	@ParameterizedTest
	@MethodSource("users")
	void findDualMajorRequirementByUser(User user) {
		//given
		//when
		GraduationRequirementJpaEntity graduationRequirementJpaEntity = requirementRepository.findDualMajorRequirementByUser(
			College.findBelongingCollege(user.getMajor()),
			user.getEntryYear());

		//then
		Assertions.assertThat(graduationRequirementJpaEntity)
			.extracting("college", "subMajorCredit")
			.contains(College.findBelongingCollege(user.getMajor()), SUB_MAJOR_CREDIT);
		Assertions.assertThat(graduationRequirementJpaEntity.getStartEntryYear())
			.isLessThanOrEqualTo(user.getEntryYear());
		Assertions.assertThat(graduationRequirementJpaEntity.getEndEntryYear())
			.isGreaterThanOrEqualTo(user.getEntryYear());
	}

	static Stream<Arguments> users() {
		return Stream.of(
			Arguments.arguments(UserFixture.영문학과_16학번()),
			Arguments.arguments(UserFixture.응용소프트웨어학과_17학번()),
			Arguments.arguments(UserFixture.데이테크놀로지학과_18학번()),
			Arguments.arguments(UserFixture.디지털콘텐츠디자인학과_23학번())
		);
	}

}
