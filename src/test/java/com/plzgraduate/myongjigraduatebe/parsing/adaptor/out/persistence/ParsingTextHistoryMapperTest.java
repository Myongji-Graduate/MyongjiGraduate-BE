package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Import(ParsingTextHistoryMapper.class)
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

		//when
		assertThat(parsingTextHistoryJpaEntity)
			.extracting("parsingText", "parsingResult")
			.contains("text", ParsingResult.SUCCESS);
	}
}
