package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.CommonCultureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class CommonCultureRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private CommonCultureRepository commonCultureRepository;

	@DisplayName("유저의 입학년도가 적용 시작 년도, 적용 마감 년도 사이에 속하는 공통 교양 과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(ints = {16, 18, 20, 23})
	void findAllByEntryYear(int entryYears) {
		//given
		LectureJpaEntity lectureJpaEntity = LectureJpaEntity.builder()
			.lectureCode("test")
			.build();
		lectureRepository.save(lectureJpaEntity);

		CommonCultureJpaEntity commonCultureJpaEntityA = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.startEntryYear(16)
			.endEntryYear(17).build();
		CommonCultureJpaEntity commonCultureJpaEntityB = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.startEntryYear(18)
			.endEntryYear(19).build();
		CommonCultureJpaEntity commonCultureJpaEntityC = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.startEntryYear(20)
			.endEntryYear(22).build();
		CommonCultureJpaEntity commonCultureJpaEntityD = CommonCultureJpaEntity.builder()
			.lectureJpaEntity(lectureJpaEntity)
			.startEntryYear(23)
			.endEntryYear(99).build();

		commonCultureRepository.saveAll(
			List.of(commonCultureJpaEntityA, commonCultureJpaEntityB, commonCultureJpaEntityC,
				commonCultureJpaEntityD));

		//when
		List<CommonCultureJpaEntity> commonCultureGraduationLectures = commonCultureRepository.findAllByEntryYear(
			entryYears);

		//then
		assertThat(commonCultureGraduationLectures).hasSize(1)
			.extracting("startEntryYear")
			.contains(entryYears);

	}
}
