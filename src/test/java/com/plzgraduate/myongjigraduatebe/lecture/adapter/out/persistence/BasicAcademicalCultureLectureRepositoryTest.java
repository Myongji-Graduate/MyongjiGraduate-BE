package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class BasicAcademicalCultureLectureRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private BasicAcademicalCultureRepository basicAcademicalCultureRepository;

	@DisplayName("유저의 단과대에 해당하는 학문기초교양과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(strings = {"인문대", "사회과학대", "경영대", "법대", "ICT융합대"})
	void findByCollege(String college) {
		//given
		LectureJpaEntity lectureJpaEntity = LectureJpaEntity.builder()
			.lectureCode("TEST")
			.build();
		lectureRepository.save(lectureJpaEntity);

		List<BasicAcademicalCultureLectureJpaEntity> basicAcademicalCultures = createBasicAcademicalCultures(
			lectureJpaEntity);
		basicAcademicalCultureRepository.saveAll(basicAcademicalCultures);

		//when
		List<BasicAcademicalCultureLectureJpaEntity> basicAcademicalCultureJpaEntities =
			basicAcademicalCultureRepository.findAllByCollege(college);

		//then
		assertThat(basicAcademicalCultureJpaEntities).extracting("college")
			.contains(college);
	}

	private List<BasicAcademicalCultureLectureJpaEntity> createBasicAcademicalCultures(
		LectureJpaEntity lectureJpaEntity) {
		BasicAcademicalCultureLectureJpaEntity humanities = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("인문대").build();
		BasicAcademicalCultureLectureJpaEntity socialScience = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("사회과학대").build();
		BasicAcademicalCultureLectureJpaEntity business = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("경영대").build();
		BasicAcademicalCultureLectureJpaEntity law = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("법대").build();
		BasicAcademicalCultureLectureJpaEntity ict = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("ICT융합대").build();
		return List.of(humanities, socialScience, business, law, ict);
	}

}
