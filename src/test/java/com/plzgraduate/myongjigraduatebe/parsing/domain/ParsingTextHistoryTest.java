package com.plzgraduate.myongjigraduatebe.parsing.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ParsingTextHistoryTest {

	@DisplayName("success 메서드 호출시 ParsingResult는 SUCCESS이다.")
	@Test
	void success() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		String text = "text";
		//when
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.success(user, text);
		//then
		assertThat(parsingTextHistory)
			.extracting("parsingText", "parsingResult")
			.contains(text, ParsingResult.SUCCESS);
	}

	@DisplayName("fail 메서드 호출시 ParsingResult는 FAIL이다.")
	@Test
	void fail() {
		//given
		User user = UserFixture.경영학과_19학번_ENG34();
		String text = "text";
		//when
		ParsingTextHistory parsingTextHistory = ParsingTextHistory.fail(user, text);
		//then
		assertThat(parsingTextHistory)
			.extracting("parsingText", "parsingResult")
			.contains(text, ParsingResult.FAIL);
	}
}
