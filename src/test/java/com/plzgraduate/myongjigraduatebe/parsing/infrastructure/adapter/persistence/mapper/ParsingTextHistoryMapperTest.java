package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity.ParsingTextHistoryJpaEntity;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ParsingTextHistoryMapperTest extends PersistenceTestSupport {

	@Autowired
	private ParsingTextHistoryMapper parsingTextHistoryMapper;

	@DisplayName("도메인 엔티티를 JPA 엔티티로 변환한다.")
	@Test
	void mapToJpaEntity() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		ParsingTextHistory parsingTextHistory = ParsingTextHistory
			.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.SUCCESS)
			.build();

		//when
		ParsingTextHistoryJpaEntity parsingTextHistoryJpaEntity = parsingTextHistoryMapper.mapToJpaEntity(
			parsingTextHistory);

		//then
		assertThat(parsingTextHistoryJpaEntity)
			.extracting("parsingText", "parsingResult")
			.contains("text", ParsingResult.SUCCESS);
	}

	@DisplayName("실패 정보를 포함한 도메인 엔티티를 JPA 엔티티로 변환한다.")
	@Test
	void mapToJpaEntity_withFailureInfo() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.FAIL)
			.failureReason(FailureReason.PARSING_EXCEPTION)
			.failureDetails("PDF 파싱 실패 상세")
			.build();

		//when
		ParsingTextHistoryJpaEntity jpaEntity = parsingTextHistoryMapper.mapToJpaEntity(parsingTextHistory);

		//then
		assertThat(jpaEntity)
			.extracting("parsingResult", "failureReason", "failureDetails")
			.contains(ParsingResult.FAIL, FailureReason.PARSING_EXCEPTION, "PDF 파싱 실패 상세");
	}

	@DisplayName("JPA 엔티티를 도메인 엔티티로 변환한다.")
	@Test
	void mapToDomainEntity() {
		//given
		UserJpaEntity userJpaEntity = UserJpaEntity.builder()
			.id(1L)
			.authId("mj21")
			.password("1234")
			.name("김경영")
			.studentNumber("60191021")
			.entryYear(19)
			.major("경영학전공")
			.transferCredit("0/0/0/0")
			.exchangeCredit("0/0/0/0/0/0/0/0")
			.build();
		ParsingTextHistoryJpaEntity jpaEntity = ParsingTextHistoryJpaEntity.builder()
			.id(1L)
			.user(userJpaEntity)
			.parsingText("text")
			.parsingResult(ParsingResult.FAIL)
			.failureReason(FailureReason.MAJOR_NAME_MISMATCH)
			.failureDetails("전공명 불일치 상세")
			.build();

		//when
		ParsingTextHistory domainEntity = parsingTextHistoryMapper.mapToDomainEntity(jpaEntity);

		//then
		assertThat(domainEntity.getId()).isEqualTo(1L);
		assertThat(domainEntity.getParsingText()).isEqualTo("text");
		assertThat(domainEntity.getParsingResult()).isEqualTo(ParsingResult.FAIL);
		assertThat(domainEntity.getFailureReason()).isEqualTo(FailureReason.MAJOR_NAME_MISMATCH);
		assertThat(domainEntity.getFailureDetails()).isEqualTo("전공명 불일치 상세");
		assertThat(domainEntity.getUser()).isNotNull();
	}
}