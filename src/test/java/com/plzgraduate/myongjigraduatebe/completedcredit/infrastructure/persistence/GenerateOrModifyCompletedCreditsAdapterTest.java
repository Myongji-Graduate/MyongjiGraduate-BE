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

	@DisplayName("이수 학점을 저장 혹은 업데이트한다.")
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

		List<CompletedCredit> completedCredits = List.of(CompletedCredit.builder()
				.id(1L)
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(10)
				.takenCredit(5)
				.build(),
			CompletedCredit.builder()
				.graduationCategory(GraduationCategory.COMMON_CULTURE)
				.totalCredit(10)
				.takenCredit(5)
				.build());

		//when
		generateOrModifyCompletedCreditsAdapter.generateOrModifyCompletedCredits(user,
			completedCredits);

		//then
		List<CompletedCreditJpaEntity> foundCompletedCredits = completedCreditRepository.findAllByUserJpaEntity(
			userJpaEntity);
		assertThat(foundCompletedCredits).hasSize(completedCredits.size())
			.extracting("userJpaEntity.id")
			.contains(user.getId());
	}
}
