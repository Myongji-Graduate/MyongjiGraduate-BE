package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity.ParsingTextHistoryJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ParsingTextHistoryMapper {

	public ParsingTextHistoryJpaEntity mapToJpaEntity(ParsingTextHistory parsingTextHistory) {

		return ParsingTextHistoryJpaEntity.builder()
			.id(parsingTextHistory.getId())
			.user(mapToUserJpaEntity(parsingTextHistory.getUser()))
			.parsingText(parsingTextHistory.getParsingText())
			.parsingResult(parsingTextHistory.getParsingResult())
			.failureReason(parsingTextHistory.getFailureReason())
			.failureDetails(parsingTextHistory.getFailureDetails())
			.build();
	}

	public ParsingTextHistory mapToDomainEntity(ParsingTextHistoryJpaEntity parsingTextHistoryJpaEntity) {
		return ParsingTextHistory.builder()
			.id(parsingTextHistoryJpaEntity.getId())
			.user(mapToUserDomainEntity(parsingTextHistoryJpaEntity.getUser()))
			.parsingText(parsingTextHistoryJpaEntity.getParsingText())
			.parsingResult(parsingTextHistoryJpaEntity.getParsingResult())
			.failureReason(parsingTextHistoryJpaEntity.getFailureReason())
			.failureDetails(parsingTextHistoryJpaEntity.getFailureDetails())
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

	private User mapToUserDomainEntity(UserJpaEntity user) {
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
			.completedSemesterCount(user.getCompletedSemesterCount())
			.transferCredit(TransferCredit.from(user.getTransferCredit()))
			.exchangeCredit(ExchangeCredit.from(user.getExchangeCredit()))
			.studentCategory(user.getStudentCategory())
			.totalCredit(user.getTotalCredit())
			.takenCredit(user.getTakenCredit())
			.graduated(user.isGraduated())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}
