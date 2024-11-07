package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory.COMMON_CULTURE;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;

class CompletedCreditPersistenceMapperTest {

	private final CompletedCreditPersistenceMapper completedCreditPersistenceMapper = new CompletedCreditPersistenceMapper();

	@DisplayName("CompletedCredit jpa entity를 domain model로 매핑한다.")
	@Test
	void mapToDomainModel() {
		//given
		CompletedCreditJpaEntity completedCreditJpaEntity = CompletedCreditJpaEntity.builder()
			.id(1L)
			.userJpaEntity(UserJpaEntity.builder()
				.id(1L)
				.build())
			.graduationCategory(COMMON_CULTURE)
			.totalCredit(10)
			.takenCredit(5)
			.build();

		//when
		CompletedCredit completedCredit = completedCreditPersistenceMapper.mapToDomainModel(
			completedCreditJpaEntity);

		//then
		assertThat(completedCredit.getGraduationCategory()).isEqualTo(
			completedCreditJpaEntity.getGraduationCategory());
		assertThat(completedCredit.getTotalCredit()).isEqualTo(
			completedCreditJpaEntity.getTotalCredit());
		assertThat(completedCredit.getTakenCredit()).isEqualTo(
			completedCreditJpaEntity.getTakenCredit());
	}

	@DisplayName("CompletedCredit 도메인 모델을 JpaEntity로 매핑한다.")
	@Test
	void mapToJpaEntity() {
		//given
		User user = User.builder()
			.id(1L)
			.build();
		CompletedCredit completedCredit = CompletedCredit.builder()
			.id(1L)
			.graduationCategory(COMMON_CULTURE)
			.totalCredit(10)
			.takenCredit(5)
			.build();

		//when
		CompletedCreditJpaEntity completedCreditJpaEntity = completedCreditPersistenceMapper.mapToJpaEntity(
			user,
			completedCredit);

		//then
		assertThat(completedCreditJpaEntity.getId()).isEqualTo(completedCredit.getId());
		assertThat(completedCreditJpaEntity.getUserJpaEntity()
			.getId()).isEqualTo(user.getId());
		assertThat(completedCreditJpaEntity.getGraduationCategory()).isEqualTo(
			completedCredit.getGraduationCategory());
		assertThat(completedCreditJpaEntity.getTotalCredit()).isEqualTo(
			completedCredit.getTotalCredit());
		assertThat(completedCreditJpaEntity.getTakenCredit()).isEqualTo(
			completedCredit.getTakenCredit());
	}

}
