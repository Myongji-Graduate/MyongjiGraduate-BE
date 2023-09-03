package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.CoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class CoreCultureRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private CoreCultureRepository coreCultureRepository;

	@DisplayName("유저의 입학년도가 적용 시작 년도, 적용 마감 년도 사이에 속하는 핵심 교양 과목을 조회한다.")
	@ParameterizedTest
	@ValueSource(ints = {16, 18})
	void findAllByEntryYear(int entryYears) {
		//given
		CoreCultureJpaEntity coreCultureJpaEntityA = CoreCultureJpaEntity.builder()
			.startEntryYear(16)
			.endEntryYear(17).build();
		CoreCultureJpaEntity coreCultureJpaEntityB = CoreCultureJpaEntity.builder()
			.startEntryYear(18)
			.endEntryYear(99).build();

		coreCultureRepository.saveAll(
			List.of(coreCultureJpaEntityA, coreCultureJpaEntityB));

		//when
		List<CoreCultureJpaEntity> coreCultureJpaEntities = coreCultureRepository.findAllByEntryYear(
			entryYears);

		//then
		assertThat(coreCultureJpaEntities).hasSize(1)
			.extracting("startEntryYear")
			.contains(entryYears);

	}

}
