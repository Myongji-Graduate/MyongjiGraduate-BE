package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GenerateOrModifyCompletedCreditsAdapterTest extends PersistenceTestSupport {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CompletedCreditRepository completedCreditRepository;
	@Autowired
	private GenerateOrModifyCompletedCreditsAdapter generateOrModifyCompletedCreditsAdapter;

	@DisplayName("기존 중복 이수 학점을 제거하고 현재 계산 결과로 교체한다.")
	@Test
	void saveOrModifyCompletedCredits() {
		//given
		UserJpaEntity userJpaEntity = userRepository.save(UserJpaEntity.builder()
			.authId("test")
			.password("test")
			.studentNumber("60191111")
			.build());
		User user = User.builder()
			.id(userJpaEntity.getId())
			.build();

		completedCreditRepository.saveAll(List.of(
			CompletedCreditJpaEntity.builder()
				.userJpaEntity(userJpaEntity)
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(12)
				.takenCredit(12)
				.build(),
			CompletedCreditJpaEntity.builder()
				.userJpaEntity(userJpaEntity)
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(12)
				.takenCredit(12)
				.build()
		));

		List<CompletedCredit> completedCredits = List.of(
			CompletedCredit.builder()
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(10)
				.takenCredit(5)
				.build()
		);

		//when
		generateOrModifyCompletedCreditsAdapter.generateOrModifyCompletedCredits(user,
			completedCredits);

		//then
		List<CompletedCreditJpaEntity> foundCompletedCredits = completedCreditRepository.findAllByUserJpaEntity(
			userJpaEntity);
		assertThat(foundCompletedCredits).hasSize(1);
		assertThat(foundCompletedCredits.getFirst().getUserJpaEntity().getId()).isEqualTo(user.getId());
		assertThat(foundCompletedCredits.getFirst().getTotalCredit()).isEqualTo(10);
		assertThat(foundCompletedCredits.getFirst().getTakenCredit()).isEqualTo(5);
	}
}
