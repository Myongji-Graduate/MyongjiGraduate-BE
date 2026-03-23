package com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.parsing.domain.FailureReason;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingTextHistory;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.entity.ParsingTextHistoryJpaEntity;
import com.plzgraduate.myongjigraduatebe.parsing.infrastructure.adapter.persistence.repository.ParsingTextRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

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
		List<ParsingTextHistoryJpaEntity> all = parsingTextRepository.findAll();
		assertThat(all).hasSize(1);
		assertThat(all.get(0))
			.extracting("parsingText", "parsingResult")
			.contains("text", ParsingResult.SUCCESS);
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

	@DisplayName("failureReason이 null인 실패 데이터를 페이징 조회한다.")
	@Test
	void findByParsingResultAndFailureReasonIsNull() {
		//given
		UserJpaEntity userJpaEntity = userRepository.save(UserJpaEntity.builder()
			.authId("authId2")
			.password("password!")
			.studentNumber("60181667")
			.transferCredit("0/0/0/0")
			.exchangeCredit("0/0/0/0/0/0/0/0")
			.build());

		// failureReason이 null인 실패 데이터
		parsingTextRepository.save(ParsingTextHistoryJpaEntity.builder()
			.user(userJpaEntity)
			.parsingText("fail1")
			.parsingResult(ParsingResult.FAIL)
			.build());

		// failureReason이 채워진 실패 데이터 (조회 제외 대상)
		parsingTextRepository.save(ParsingTextHistoryJpaEntity.builder()
			.user(userJpaEntity)
			.parsingText("fail2")
			.parsingResult(ParsingResult.FAIL)
			.failureReason(FailureReason.PARSING_EXCEPTION)
			.failureDetails("상세")
			.build());

		// 성공 데이터 (조회 제외 대상)
		parsingTextRepository.save(ParsingTextHistoryJpaEntity.builder()
			.user(userJpaEntity)
			.parsingText("success")
			.parsingResult(ParsingResult.SUCCESS)
			.build());

		entityManager.flush();
		entityManager.clear();

		//when
		List<ParsingTextHistory> results = parsingTextHistoryAdapter
			.findByParsingResultAndFailureReasonIsNull(PageRequest.of(0, 100));

		//then
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getParsingText()).isEqualTo("fail1");
		assertThat(results.get(0).getFailureReason()).isNull();
	}
}
