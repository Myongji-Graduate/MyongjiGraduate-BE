package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Import({LectureMapper.class, FindBasicAcademicalCulturePersistenceAdapter.class})
class FindBasicAcademicalCulturePersistenceAdapterTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private BasicAcademicalCultureRepository basicAcademicalCultureRepository;
	@Autowired
	private FindBasicAcademicalCulturePersistenceAdapter basicAcademicalCulturePersistenceAdapter;

	@DisplayName("유저의 단과대에 속하는 학문기초교양 과목들을 반환한다.")
	@Test
	void findBasicAcademicalCulture() {
		//given
		User user = UserFixture.응용소프트웨어학과_19학번();
		LectureJpaEntity lectureJpaEntityA = LectureJpaEntity.builder()
			.lectureCode("testA")
			.build();
		LectureJpaEntity lectureJpaEntityB = LectureJpaEntity.builder()
			.lectureCode("testB")
			.build();
		lectureRepository.saveAll(List.of(lectureJpaEntityA, lectureJpaEntityB));

		BasicAcademicalCultureLectureJpaEntity basicAcademicalCultureLectureJpaEntityA = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.college(ICT.getText()).build();

		BasicAcademicalCultureLectureJpaEntity basicAcademicalCultureLectureJpaEntityB = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.college(BUSINESS.getText()).build();
		basicAcademicalCultureRepository.saveAll(
			List.of(basicAcademicalCultureLectureJpaEntityA, basicAcademicalCultureLectureJpaEntityB));

		//when
		Set<BasicAcademicalCultureLecture> basicAcademicalCulture = basicAcademicalCulturePersistenceAdapter.findBasicAcademicalCulture(
			user);
		//then
		assertThat(basicAcademicalCulture).hasSize(1)
			.extracting("college")
			.contains(ICT.getText());
	}

}
