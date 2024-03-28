package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;

class CompletedCreditPersistenceMapperTest {

	private final CompletedCreditPersistenceMapper completedCreditPersistenceMapper = new CompletedCreditPersistenceMapper();

	@DisplayName("CompletedCredit jpa entity를 domain model로 매핑한다.")
	@Test
	void mapToDomainModel() {
		//given
		CompletedCreditJpaEntity completedCreditJpaEntity = CompletedCreditJpaEntity.builder()
			.id(1L)
			.category(GraduationCategory.COMMON_CULTURE)
			.totalCredit(10)
			.takenCredit(5).build();

		//when
		CompletedCredit completedCredit = completedCreditPersistenceMapper.mapToDomainModel(completedCreditJpaEntity);

		//then
		assertThat(completedCredit.getCategory()).isEqualTo(completedCreditJpaEntity.getCategory());
		assertThat(completedCredit.getTotalCredit()).isEqualTo(completedCreditJpaEntity.getTotalCredit());
		assertThat(completedCredit.getTakenCredit()).isEqualTo(completedCreditJpaEntity.getTakenCredit());
	}

}
