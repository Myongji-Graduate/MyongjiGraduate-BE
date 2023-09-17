package com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity.GraduationRequirementJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Import({FindGraduationRequirementPersistenceAdapter.class, GraduationRequirementMapper.class})
class FindGraduationRequirementPersistenceAdapterTest extends PersistenceTestSupport {

	private static final int SUB_MAJOR_CREDIT = 10;

	@Autowired
	private GraduationRequirementRepository graduationRequirementRepository;
	@Autowired
	private FindGraduationRequirementPersistenceAdapter loadGraduationRequirementPersistenceAdapter;

	@DisplayName("단일 전공 유저의 입학년도에 해당하는 졸업요건을 반환한다.")
	@Test
	void findSingleMajorUserGraduationRequirement() {
		//given
		List<GraduationRequirementJpaEntity> graduationRequirements = createBusinessGraduationRequirements();
		graduationRequirementRepository.saveAll(graduationRequirements);

		User user = UserFixture.경영학과_19학번_ENG34();

		//when
		GraduationRequirement graduationRequirement = loadGraduationRequirementPersistenceAdapter.findGraduationRequirement(
			user);

		//then
		Assertions.assertThat(graduationRequirement)
			.extracting("commonCultureCredit", "coreCultureCredit", "basicAcademica"
				+ "lCredit", "normalCultureCredit", "majorCredit", "freeElectiveCredit")
			.contains(17, 12, 6, 10, 63, 20);
	}

	@DisplayName("복수 전공 유저의 입학년도에 해당하는 졸업요건을 반환한다.")
	@Test
	void findDualMajorUserGraduationRequirement() {
		//given
		List<GraduationRequirementJpaEntity> graduationRequirements = createBusinessGraduationRequirements();
		graduationRequirementRepository.saveAll(graduationRequirements);

		User user = UserFixture.경영학과_19학번_ENG34_복수전공();

		//when
		GraduationRequirement graduationRequirement = loadGraduationRequirementPersistenceAdapter.findGraduationRequirement(
			user);

		//then
		Assertions.assertThat(graduationRequirement)
			.extracting("subMajorCredit", "commonCultureCredit", "coreCultureCredit", "basicAcademica"
				+ "lCredit", "normalCultureCredit", "majorCredit", "freeElectiveCredit")
			.contains(SUB_MAJOR_CREDIT, 15, 12, 6, 10, 63, 22);
	}

	@DisplayName("영어 면제 유저의 졸업요건은 공통교양의 영어 카테고리 학점(6)이 일반교양 학점으로 이관된 졸업 요건이다.")
	@Test
	void findFreeEnglishLevelUserGraduationRequirement() {
		//given
		List<GraduationRequirementJpaEntity> graduationRequirements = createBusinessGraduationRequirements();
		graduationRequirementRepository.saveAll(graduationRequirements);

		User user = UserFixture.경영학과_19학번_영어_면제();

		//when
		GraduationRequirement graduationRequirement = loadGraduationRequirementPersistenceAdapter.findGraduationRequirement(
			user);

		//then
		Assertions.assertThat(graduationRequirement)
			.extracting("commonCultureCredit", "normalCultureCredit")
			.contains(11, 16);
	}

	private List<GraduationRequirementJpaEntity> createBusinessGraduationRequirements() {

		//16~17 경영대 졸업 요건
		GraduationRequirementJpaEntity graduationRequirementJpaEntityA = GraduationRequirementJpaEntity.builder()
			.college(BUSINESS)
			.subMajorCredit(0)
			.startEntryYear(16)
			.endEntryYear(17)
			.commonCultureCredit(15)
			.coreCultureCredit(12)
			.basicAcademicalCredit(6)
			.normalCultureCredit(10)
			.majorCredit(63)
			.freeElectiveCredit(22).build();

		GraduationRequirementJpaEntity graduationRequirementJpaEntityB = GraduationRequirementJpaEntity.builder()
			.college(BUSINESS)
			.subMajorCredit(SUB_MAJOR_CREDIT)
			.startEntryYear(16)
			.endEntryYear(17).commonCultureCredit(15)
			.coreCultureCredit(12)
			.basicAcademicalCredit(6)
			.normalCultureCredit(10)
			.majorCredit(63)
			.freeElectiveCredit(22).build();

		//18~23 경영대 졸업 요건
		GraduationRequirementJpaEntity graduationRequirementJpaEntityC = GraduationRequirementJpaEntity.builder()
			.college(BUSINESS)
			.subMajorCredit(0)
			.startEntryYear(18)
			.endEntryYear(23)
			.commonCultureCredit(17)
			.coreCultureCredit(12)
			.basicAcademicalCredit(6)
			.normalCultureCredit(10)
			.majorCredit(63)
			.freeElectiveCredit(20).build();

		GraduationRequirementJpaEntity graduationRequirementJpaEntityD = GraduationRequirementJpaEntity.builder()
			.college(BUSINESS)
			.subMajorCredit(SUB_MAJOR_CREDIT)
			.startEntryYear(18)
			.endEntryYear(23)
			.commonCultureCredit(15)
			.coreCultureCredit(12)
			.basicAcademicalCredit(6)
			.normalCultureCredit(10)
			.majorCredit(63)
			.freeElectiveCredit(22).build();

		return List.of(graduationRequirementJpaEntityA, graduationRequirementJpaEntityB,
			graduationRequirementJpaEntityC, graduationRequirementJpaEntityD);
	}

}
