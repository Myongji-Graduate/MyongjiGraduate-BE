package com.plzgraduate.myongjigraduatebe.parsing.adaptor.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class ParsingTextHistoryAdaptorTest extends PersistenceTestSupport {

	@Autowired
	private ParsingTextRepository parsingTextRepository;

	@Autowired
	private ParsingTextHistoryAdaptor parsingTextHistoryAdaptor;

	@Autowired
	private UserRepository userRepository;

	@DisplayName("ParsingTextHistory를 저장한다.")
	@Test
	void saveParsingTextHistory() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.builder()
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

	@DisplayName("유저의 ParsingHistory를 삭제한다.")
	@Test
	@Transactional
	void deleteUserParsingTextHistory() {
	    //given
		User user = User.builder().id(1L).build();
		userRepository.save(UserJpaEntity.builder()
			.id(1L)
			.studentNumber("test")
			.authId("test")
			.password("test").build());
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.SUCCESS)
			.build();
		parsingTextHistoryAdaptor.saveParsingTextHistory(parsingTextHistory);

		//when
		parsingTextHistoryAdaptor.deleteUserParsingTextHistory(user);

	    //then
		assertThat(parsingTextRepository.findAll()).isEmpty();
	}
}
