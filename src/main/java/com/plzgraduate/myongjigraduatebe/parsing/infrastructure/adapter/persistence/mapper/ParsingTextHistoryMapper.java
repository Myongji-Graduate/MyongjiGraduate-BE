package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.mapper;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity.ParsingTextHistoryJpaEntity;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Component
public class ParsingTextHistoryMapper {

	public ParsingTextHistoryJpaEntity mapToJpaEntity(ParsingTextHistory parsingTextHistory) {

		return ParsingTextHistoryJpaEntity.builder()
			.id(parsingTextHistory.getId())
			.user(mapToUserJpaEntity(parsingTextHistory.getUser()))
			.parsingText(parsingTextHistory.getParsingText())
			.parsingResult(parsingTextHistory.getParsingResult())
			.build();
	}

	public UserJpaEntity mapToUserJpaEntity(User user) {

		return UserJpaEntity.builder()
			.id(user.getId())
			.authId(user.getAuthId())
			.password(user.getPassword())
			.name(user.getName())
			.englishLevel(user.getEnglishLevel())
			.studentNumber(user.getStudentNumber())
			.entryYear(user.getEntryYear())
			.major(user.getPrimaryMajor())
			.subMajor(user.getSubMajor())
			.studentCategory(user.getStudentCategory())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}
