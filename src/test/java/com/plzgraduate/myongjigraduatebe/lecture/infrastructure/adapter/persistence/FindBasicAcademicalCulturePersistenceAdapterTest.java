package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.BUSINESS;
import static com.plzgraduate.myongjigraduatebe.user.domain.model.College.ICT;
import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.BasicAcademicalCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FindBasicAcademicalCulturePersistenceAdapterTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private BasicAcademicalCultureRepository basicAcademicalCultureRepository;
	@Autowired
	private FindBasicAcademicalCulturePersistenceAdapter basicAcademicalCulturePersistenceAdapter;

	@DisplayName("전공학과의 단과대에 속하는 학문기초교양 과목들을 반환한다.")
	@Test
	void findBasicAcademicalCulture() {
		//given
		User user = UserFixture.응용소프트웨어전공_19학번();
		LectureJpaEntity lectureJpaEntityA = LectureJpaEntity.builder()
			.id("testA")
			.build();
		LectureJpaEntity lectureJpaEntityB = LectureJpaEntity.builder()
			.id("testB")
			.build();
		lectureRepository.saveAll(List.of(lectureJpaEntityA, lectureJpaEntityB));

		BasicAcademicalCultureLectureJpaEntity basicAcademicalCultureLectureJpaEntityA = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.college(ICT.getName())
			.build();

		BasicAcademicalCultureLectureJpaEntity basicAcademicalCultureLectureJpaEntityB = BasicAcademicalCultureLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.college(BUSINESS.getName())
			.build();
		basicAcademicalCultureRepository.saveAll(
			List.of(
				basicAcademicalCultureLectureJpaEntityA,
				basicAcademicalCultureLectureJpaEntityB
			));

		//when
		Set<BasicAcademicalCultureLecture> basicAcademicalCulture = basicAcademicalCulturePersistenceAdapter.findBasicAcademicalCulture(
			user.getPrimaryMajor(), user.getEntryYear());
		//then
		assertThat(basicAcademicalCulture).hasSize(1)
			.extracting("college")
			.contains(ICT.getName());
	}

}
