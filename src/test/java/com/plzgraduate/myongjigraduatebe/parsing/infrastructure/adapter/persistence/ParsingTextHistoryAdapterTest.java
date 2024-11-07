package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity.ParsingTextHistoryJpaEntity;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.repository.ParsingTextRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ParsingTextHistoryAdapterTest extends PersistenceTestSupport {

	@Autowired
	private ParsingTextRepository parsingTextRepository;

	@Autowired
	private ParsingTextHistoryAdapter parsingTextHistoryAdapter;

	@Autowired
	private UserRepository userRepository;

	@DisplayName("ParsingTextHistory를 저장한다.")
	@Test
	void saveParsingTextHistory() {
		//given
		UserJpaEntity userJpaEntity = userRepository.save(UserJpaEntity.builder()
			.authId("authId")
			.password("password!")
			.studentNumber("60181666")
			.build());
		User user = User.builder()
			.id(userJpaEntity.getId())
			.build();
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.SUCCESS)
			.build();

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
		UserJpaEntity userJpaEntity = userRepository.save(UserJpaEntity.builder()
			.studentNumber("test")
			.authId("test")
			.password("test")
			.build());
		User user = User.builder()
			.id(userJpaEntity.getId())
			.authId("test")
			.password("test")
			.studentNumber("test")
			.build();
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.builder()
			.user(user)
			.parsingText("text")
			.parsingResult(ParsingResult.SUCCESS)
			.build();
		parsingTextHistoryAdapter.saveParsingTextHistory(parsingTextHistory);

		//when
		parsingTextHistoryAdapter.deleteUserParsingTextHistory(user);

		//then
		assertThat(parsingTextRepository.findAll()).isEmpty();
	}
}
