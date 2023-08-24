package com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Component
public class UserMapper {

	User mapToDomainEntity(UserJpaEntity user) {

		return User.builder()
			.id(user.getId())
			.userId(user.getAuthId())
			.password(user.getPassword())
			.name(user.getName())
			.studentNumber(user.getStudentNumber())
			.entryYear(user.getEntryYear())
			.englishLevel(user.getEnglishLevel())
			.major(user.getMajor())
			.subMajor(user.getSubMajor())
			.build();
	}

	UserJpaEntity mapToJpaEntity(User user) {

		return UserJpaEntity.builder()
			.id(user.getId())
			.authId(user.getUserId())
			.password(user.getPassword())
			.englishLevel(user.getEnglishLevel())
			.studentNumber(user.getStudentNumber())
			.entryYear(user.getEntryYear())
			.major(user.getMajor())
			.subMajor(user.getSubMajor())
			.studentCategory(user.getStudentCategory())
			.build();
	}

}
