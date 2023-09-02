package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@Import({ParsingTextHistoryAdaptor.class, ParsingTextHistoryMapper.class})
class ParsingTextHistoryAdaptorTest extends PersistenceTestSupport {

	@Autowired
	private ParsingTextRepository parsingTextRepository;

	@Autowired
	private ParsingTextHistoryAdaptor parsingTextHistoryAdaptor;

	@DisplayName("ParsingTextHistory를 저장한다.")
	@Test
	void saveParsingTextHistory() {
		//given
		User user = UserFixture.경영학과_19학번();
		ParsingTextHistory parsingTextHistory = ParsingTextHistory
			.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.SUCCESS)
			.build();

		//when
		parsingTextHistoryAdaptor.saveParsingTextHistory(parsingTextHistory);

		//then
		Optional<ParsingTextHistoryJpaEntity> byId =
			parsingTextRepository.findById(1L);
		assertThat(byId).isPresent();
		assertThat(byId.get())
			.extracting("id", "parsingText", "parsingResult")
			.contains(1L, "text", ParsingResult.SUCCESS);
	}
}
