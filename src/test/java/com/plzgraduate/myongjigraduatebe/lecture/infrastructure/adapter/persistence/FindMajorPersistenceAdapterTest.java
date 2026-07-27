package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.MajorLectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FindMajorPersistenceAdapterTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private MajorLectureRepository majorLectureRepository;
	@Autowired
	private FindMajorPersistenceAdapter majorPersistenceAdapter;

	@DisplayName("유저의 전공에 해당하는 전공 과목을 조회한다.")
	@Test
	void findMajor() {
		//given
		User user = UserFixture.응용소프트웨어전공_19학번();
		LectureJpaEntity lectureJpaEntityA = LectureJpaEntity.builder()
			.id("testA")
			.build();
		LectureJpaEntity lectureJpaEntityB = LectureJpaEntity.builder()
			.id("testB")
			.build();
		lectureRepository.saveAll(List.of(lectureJpaEntityA, lectureJpaEntityB));

		MajorLectureJpaEntity majorLectureJpaEntityA = MajorLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.major(user.getPrimaryMajor())
			.build();

		MajorLectureJpaEntity majorLectureJpaEntityB = MajorLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntityA)
			.major("데이터테크놀로지")
			.build();
		majorLectureRepository.saveAll(List.of(majorLectureJpaEntityA, majorLectureJpaEntityB));

		//when
		Set<MajorLecture> majors = majorPersistenceAdapter.findMajor(user.getPrimaryMajor());

		//then
		assertThat(majors).hasSize(1)
			.extracting("major")
			.contains(user.getPrimaryMajor());
	}

	@DisplayName("국제통상학전공으로 조회해도 국제통상학과 전공 과목을 함께 조회한다.")
	@Test
	void findMajor_alias() {
		//given
		LectureJpaEntity lectureJpaEntity = LectureJpaEntity.builder()
			.id("HBH01101")
			.name("국제통상원론")
			.credit(3)
			.isRevoked(0)
			.build();
		lectureRepository.save(lectureJpaEntity);

		MajorLectureJpaEntity majorLectureJpaEntity = MajorLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.major("국제통상학과")
			.mandatory(1)
			.startEntryYear(25)
			.endEntryYear(99)
			.build();
		majorLectureRepository.save(majorLectureJpaEntity);

		//when
		Set<MajorLecture> majors = majorPersistenceAdapter.findMajor("국제통상학전공");

		//then
		assertThat(majors).hasSize(1)
			.extracting("major")
			.contains("국제통상학과");
	}

}
