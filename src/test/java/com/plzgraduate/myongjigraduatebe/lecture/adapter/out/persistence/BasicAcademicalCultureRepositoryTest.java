package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.BasicAcademicalCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class BasicAcademicalCultureRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private BasicAcademicalCultureRepository basicAcademicalCultureRepository;

	@DisplayName("유저의 단과대에 해당하는 학문기초교양과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(strings = {"인문대", "사회과학대", "경영대", "법대", "ICT융합대"})
	void findByCollege(String college) {
		//given
		List<BasicAcademicalCultureJpaEntity> basicAcademicalCultures = createBasicAcademicalCultures();
		basicAcademicalCultureRepository.saveAll(basicAcademicalCultures);

		//when
		List<BasicAcademicalCultureJpaEntity> basicAcademicalCultureJpaEntities =
			basicAcademicalCultureRepository.findAllByCollege(college);

		//then
		assertThat(basicAcademicalCultureJpaEntities).extracting("college")
			.contains(college);
	}

	private List<BasicAcademicalCultureJpaEntity> createBasicAcademicalCultures() {
		BasicAcademicalCultureJpaEntity humanities = BasicAcademicalCultureJpaEntity.builder()
			.college("인문대").build();
		BasicAcademicalCultureJpaEntity socialScience = BasicAcademicalCultureJpaEntity.builder()
			.college("사회과학대").build();
		BasicAcademicalCultureJpaEntity business = BasicAcademicalCultureJpaEntity.builder()
			.college("경영대").build();
		BasicAcademicalCultureJpaEntity law = BasicAcademicalCultureJpaEntity.builder()
			.college("법대").build();
		BasicAcademicalCultureJpaEntity ict = BasicAcademicalCultureJpaEntity.builder()
			.college("ICT융합대").build();
		return List.of(humanities, socialScience, business, law, ict);
	}

}
