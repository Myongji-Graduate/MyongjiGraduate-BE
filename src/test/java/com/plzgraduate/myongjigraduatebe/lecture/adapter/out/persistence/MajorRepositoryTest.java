package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.MajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.MajorRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class MajorRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private MajorRepository majorRepository;

	@DisplayName("유저의 전공에 해당하는 전공 과목을 조회한다.")
	@Test
	void findAllByMajor() {
	    //given
		MajorLectureJpaEntity majorLectureJpaEntityA = MajorLectureJpaEntity.builder()
			.major("응용소프트웨어").build();
		MajorLectureJpaEntity majorLectureJpaEntityB = MajorLectureJpaEntity.builder()
			.major("데이터테크놀로지").build();
		majorRepository.saveAll(List.of(majorLectureJpaEntityA, majorLectureJpaEntityB));

		String major = "응용소프트웨어";

	    //when
		List<MajorLectureJpaEntity> majorLectures = majorRepository.findAllByMajor(major);

		//then
		assertThat(majorLectures).hasSize(1)
			.extracting("major")
			.contains(major);
	}

}
