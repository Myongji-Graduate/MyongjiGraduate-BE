package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
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

}
