package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.repository.CompletedCreditRepository;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;

class FindCompletedCreditAdapterTest extends PersistenceTestSupport {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private CompletedCreditRepository completedCreditRepository;
	@Autowired
	private FindCompletedCreditAdapter findCompletedCreditAdapter;

	
	@DisplayName("유저의 이수학점을 조회 후 도메인 모델로 반환한다.")
	@Test
	void findCompletedCredit() {
	    //given
		UserJpaEntity userJpaEntity = UserJpaEntity.builder()
			.id(1L)
			.authId("test")
			.password("test")
			.studentNumber("60191111").build();

		CompletedCreditJpaEntity commonCultureCompletedCreditJpaEntity = CompletedCreditJpaEntity.builder()
			.userJpaEntity(userJpaEntity)
			.category(GraduationCategory.COMMON_CULTURE)
			.totalCredit(10)
			.takenCredit(10).build();

		CompletedCreditJpaEntity coreCultureCompletedCreditJpaEntity = CompletedCreditJpaEntity.builder()
			.userJpaEntity(userJpaEntity)
			.category(GraduationCategory.CORE_CULTURE)
			.totalCredit(10)
			.takenCredit(10).build();

		userRepository.save(userJpaEntity);
		completedCreditRepository.saveAll(
			List.of(commonCultureCompletedCreditJpaEntity, coreCultureCompletedCreditJpaEntity));

		//when
		List<CompletedCredit> completedCredits = findCompletedCreditAdapter.findCompletedCredit(
			userMapper.mapToDomainEntity(userJpaEntity));

		//then
		assertThat(completedCredits).hasSize(2);
	}

}
