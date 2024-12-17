package com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

	public User mapToDomainEntity(UserJpaEntity user) {
		return User.builder()
			.id(user.getId())
			.authId(user.getAuthId())
			.password(user.getPassword())
			.name(user.getName())
			.studentNumber(user.getStudentNumber())
			.entryYear(user.getEntryYear())
			.englishLevel(user.getEnglishLevel())
			.primaryMajor(user.getMajor())
			.dualMajor(user.getDualMajor())
			.subMajor(user.getSubMajor())
			.associatedMajor(user.getAssociatedMajor())
			.transferCredit(TransferCredit.from(user.getTransferCredit()))
			.studentCategory(user.getStudentCategory())
			.totalCredit(user.getTotalCredit())
			.takenCredit(user.getTakenCredit())
			.graduated(user.isGraduated())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}

	public UserJpaEntity mapToJpaEntity(User user) {

		return UserJpaEntity.builder()
			.id(user.getId())
			.authId(user.getAuthId())
			.password(user.getPassword())
			.name(user.getName())
			.englishLevel(user.getEnglishLevel())
			.studentNumber(user.getStudentNumber())
			.entryYear(user.getEntryYear())
			.major(user.getPrimaryMajor())
			.dualMajor(user.getDualMajor())
			.subMajor(user.getSubMajor())
			.associatedMajor(user.getAssociatedMajor())
			.transferCredit(user.getTransferCredit().toString())
			.studentCategory(user.getStudentCategory())
			.totalCredit(user.getTotalCredit())
			.takenCredit(user.getTakenCredit())
			.graduated(user.isGraduated())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}

}
