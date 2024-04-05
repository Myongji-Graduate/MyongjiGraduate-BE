package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;

class CompletedCreditRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CompletedCreditRepository completedCreditRepository;

	@DisplayName("유저의 이수 학점을 조회 한다.")
	@Test
	void findAllByUserJpaEntity() {
		//given
		UserJpaEntity userJpaEntity = UserJpaEntity.builder()
			.authId("test1234")
			.password("test")
			.studentNumber("60191112").build();
		userRepository.save(userJpaEntity);

		CompletedCreditJpaEntity commonCultureCompletedCreditJpaEntity = CompletedCreditJpaEntity.builder()
			.userJpaEntity(userJpaEntity)
			.graduationCategory(GraduationCategory.COMMON_CULTURE)
			.totalCredit(10)
			.takenCredit(10).build();

		CompletedCreditJpaEntity coreCultureCompletedCreditJpaEntity = CompletedCreditJpaEntity.builder()
			.userJpaEntity(userJpaEntity)
			.graduationCategory(GraduationCategory.CORE_CULTURE)
			.totalCredit(10)
			.takenCredit(10).build();

		completedCreditRepository.saveAll(
			List.of(commonCultureCompletedCreditJpaEntity, coreCultureCompletedCreditJpaEntity));

		//when
		List<CompletedCreditJpaEntity> foundCompletedCredits = completedCreditRepository.findAllByUserJpaEntity(
			userJpaEntity);

		//then
		Assertions.assertThat(foundCompletedCredits).hasSize(2)
			.extracting("userJpaEntity.authId")
			.contains(userJpaEntity.getAuthId());
	}

}
