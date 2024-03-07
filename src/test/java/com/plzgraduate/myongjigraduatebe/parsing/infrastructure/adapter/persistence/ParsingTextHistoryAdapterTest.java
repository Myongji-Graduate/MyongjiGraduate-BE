package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.ParsingTextHistoryAdapter;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity.ParsingTextHistoryJpaEntity;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.repository.ParsingTextRepository;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

class ParsingTextHistoryAdapterTest extends PersistenceTestSupport {

	@Autowired
	private ParsingTextRepository parsingTextRepository;

	@Autowired
	private ParsingTextHistoryAdapter parsingTextHistoryAdapter;

	@Autowired
	private UserRepository userRepository;

	@AfterEach
	void afterEach() {
		this.entityManager
			.createNativeQuery("ALTER TABLE parsing_text_history AUTO_INCREMENT = 1")
			.executeUpdate();
	}

	@DisplayName("ParsingTextHistory를 저장한다.")
	@Test
	void saveParsingTextHistory() {
		//given
		User user = User.builder()
			.id(1L)
			.build();

		ParsingTextHistory parsingTextHistory = ParsingTextHistory.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.SUCCESS)
			.build();

		userRepository.save(UserJpaEntity.builder()
			.id(1L)
			.authId("authId")
			.password("password!")
			.studentNumber("60181666")
			.build());

		//when
		parsingTextHistoryAdapter.saveParsingTextHistory(parsingTextHistory);

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
	void deleteUserParsingTextHistory() {
	    //given
		User user = User.builder()
			.id(1L)
			.authId("test")
			.password("test")
			.studentNumber("test")
			.build();
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.SUCCESS)
			.build();
		userRepository.save(UserJpaEntity.builder()
			.id(1L)
			.studentNumber("test")
			.authId("test")
			.password("test")
			.build());
		parsingTextHistoryAdapter.saveParsingTextHistory(parsingTextHistory);

		//when
		parsingTextHistoryAdapter.deleteUserParsingTextHistory(user);

	    //then
		assertThat(parsingTextRepository.findAll()).isEmpty();
	}
}
