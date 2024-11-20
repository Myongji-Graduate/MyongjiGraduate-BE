package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.BUSINESS;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.ICT;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.TakenLectureRepository;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class BasicAcademicalCultureLectureRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TakenLectureRepository takenLectureRepository;
	@Autowired
	private BasicAcademicalCultureRepository basicAcademicalCultureRepository;

	@DisplayName("유저의 단과대에 해당하는 학문기초교양과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(strings = {"인문대", "사회과학대", "경영대", "법대", "ICT융합대"})
	void findByCollege(String college) {
		//given
		LectureJpaEntity lectureJpaEntity = LectureJpaEntity.builder()
			.id("TEST")
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

	@DisplayName("유저가 수강한 과목 중 유저의 주전공, 복수전공 양쪽 모두에 해당하는 학문기초교양의 수를 조회한다.")
	@Test
	void findAllDuplicatedTakenCountByCollages() {
		//given
		UserJpaEntity userJpaEntity = userRepository.save(UserJpaEntity.builder()
			.authId("test")
			.password("test")
			.studentNumber("12341234")
			.major("응용소프트웨어전공")
			.dualMajor("경영학과")
			.build());

		LectureJpaEntity lectureJpaEntityA = LectureJpaEntity.builder()
			.id("TESTA")
			.build();
		LectureJpaEntity lectureJpaEntityB = LectureJpaEntity.builder()
			.id("TESTB")
			.build();
		List<LectureJpaEntity> lectureJpaEntities = lectureRepository.saveAll(
			List.of(lectureJpaEntityA, lectureJpaEntityB));

		TakenLectureJpaEntity takenLectureJpaEntityA = TakenLectureJpaEntity.builder()
			.user(userJpaEntity)
			.lecture(lectureJpaEntities.get(0))
			.build();
		TakenLectureJpaEntity takenLectureJpaEntityB = TakenLectureJpaEntity.builder()
			.user(userJpaEntity)
			.lecture(lectureJpaEntities.get(1))
			.build();
		takenLectureRepository.saveAll(List.of(takenLectureJpaEntityA, takenLectureJpaEntityB));

		BasicAcademicalCultureLectureJpaEntity ictBasicAcademicalCultureJpaEntity = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntities.get(0))
			.college(ICT.getName())
			.build();
		BasicAcademicalCultureLectureJpaEntity businessBasicAcademicalCultureJpaEntity = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntities.get(1))
			.college(BUSINESS.getName())
			.build();
		BasicAcademicalCultureLectureJpaEntity bothBasicAcademicalCultureJpaEntity = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntities.get(0))
			.college(BUSINESS.getName())
			.build();
		basicAcademicalCultureRepository.saveAll(
			List.of(ictBasicAcademicalCultureJpaEntity, businessBasicAcademicalCultureJpaEntity,
				bothBasicAcademicalCultureJpaEntity));

		//when
		List<BasicAcademicalCultureLectureJpaEntity> duplicatedTakenBasicAcademicalCultures = basicAcademicalCultureRepository.findAllDuplicatedTakenByCollages(
			userJpaEntity.getId(), ICT.getName(), BUSINESS.getName());

		//then
		assertThat(duplicatedTakenBasicAcademicalCultures).hasSize(1);
	}

	private List<BasicAcademicalCultureLectureJpaEntity> createBasicAcademicalCultures(
		LectureJpaEntity lectureJpaEntity) {
		BasicAcademicalCultureLectureJpaEntity humanities = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("인문대")
			.build();
		BasicAcademicalCultureLectureJpaEntity socialScience = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("사회과학대")
			.build();
		BasicAcademicalCultureLectureJpaEntity business = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("경영대")
			.build();
		BasicAcademicalCultureLectureJpaEntity law = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("법대")
			.build();
		BasicAcademicalCultureLectureJpaEntity ict = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.college("ICT융합대")
			.build();
		return List.of(humanities, socialScience, business, law, ict);
	}

}
