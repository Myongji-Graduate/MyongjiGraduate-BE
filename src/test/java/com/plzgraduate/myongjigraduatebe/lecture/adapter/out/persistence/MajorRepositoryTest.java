package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.MajorJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class MajorRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private MajorRepository majorRepository;

	@DisplayName("유저의 전공에 해당하는 전공 과목을 조회한다.")
	@Test
	void findAllByMajor() {
	    //given
		MajorJpaEntity majorJpaEntityA = MajorJpaEntity.builder()
			.major("응용소프트웨어").build();
		MajorJpaEntity majorJpaEntityB = MajorJpaEntity.builder()
			.major("데이터테크놀로지").build();
		majorRepository.saveAll(List.of(majorJpaEntityA, majorJpaEntityB));

		String major = "응용소프트웨어";

	    //when
		List<MajorJpaEntity> majorLectures = majorRepository.findAllByMajor(major);

		//then
		assertThat(majorLectures).hasSize(1)
			.extracting("major")
			.contains(major);
	}

}
