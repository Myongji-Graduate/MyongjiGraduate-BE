package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MajorLectureRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private MajorLectureRepository majorLectureRepository;


	@DisplayName("유저의 전공에 해당하는 전공 과목을 조회한다.")
	@Test
	void findAllByMajor() {
		//given
		LectureJpaEntity lectureJpaEntity = LectureJpaEntity.builder()
			.id("TEST")
			.build();
		lectureRepository.save(lectureJpaEntity);

		MajorLectureJpaEntity majorLectureJpaEntityA = MajorLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.major("응용소프트웨어")
			.build();
		MajorLectureJpaEntity majorLectureJpaEntityB = MajorLectureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.major("데이터테크놀로지")
			.build();
		majorLectureRepository.saveAll(List.of(majorLectureJpaEntityA, majorLectureJpaEntityB));

		String major = "응용소프트웨어";

		//when
		List<MajorLectureJpaEntity> majorLectures = majorLectureRepository.findAllByMajor(major);

		//then
		assertThat(majorLectures).hasSize(1)
			.extracting("major")
			.contains(major);
	}

}
