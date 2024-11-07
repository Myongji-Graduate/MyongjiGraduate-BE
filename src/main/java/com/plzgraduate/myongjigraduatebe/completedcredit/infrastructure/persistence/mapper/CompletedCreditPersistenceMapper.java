package com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.completedcredit.infrastructure.persistence.entity.CompletedCreditJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CompletedCreditPersistenceMapper {

	public CompletedCredit mapToDomainModel(CompletedCreditJpaEntity completedCreditJpaEntity) {
		return CompletedCredit.builder()
			.id(completedCreditJpaEntity.getId())
			.graduationCategory(completedCreditJpaEntity.getGraduationCategory())
			.totalCredit(completedCreditJpaEntity.getTotalCredit())
			.takenCredit(completedCreditJpaEntity.getTakenCredit())
			.build();
	}

	public CompletedCreditJpaEntity mapToJpaEntity(User user, CompletedCredit completedCredit) {
		return CompletedCreditJpaEntity.builder()
			.id(completedCredit.getId())
			.graduationCategory(completedCredit.getGraduationCategory())
			.userJpaEntity(UserJpaEntity.builder()
				.id(user.getId())
				.build())
			.totalCredit(completedCredit.getTotalCredit())
			.takenCredit(completedCredit.getTakenCredit())
			.build();
	}
}
