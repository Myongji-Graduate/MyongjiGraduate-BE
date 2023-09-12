package com.plzgraduate.myongjigraduatebe.user.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserTest {

	@Mock
	private PasswordEncoder passwordEncoder;

	private User user;

	@BeforeEach
	void setUp() {
		openMocks(this);
		user = User.builder()
			.authId("tester00")
			.password("tester00!")
			.name("테스터")
			.studentNumber("60201000")
			.entryYear(20)
			.englishLevel(EnglishLevel.ENG12)
			.major("경영학과")
			.studentCategory(StudentCategory.NORMAL)
			.build();
	}

	@DisplayName("create 메서드 테스트")
	@Test
	void create() {
		//when
		User newUser = User.create("tester00", "tester00!", EnglishLevel.ENG12, "60201000");

		//then
		assertThat(newUser)
			.extracting("authId", "password", "englishLevel", "studentNumber", "entryYear")
			.contains("tester00", "tester00!", EnglishLevel.ENG12, "60201000", 20);
	}

	@DisplayName("updateStudentInformation 메서드 테스트")
	@Test
	void updateStudentInformation() {
		//given //when
		user.updateStudentInformation("테스터2", "경영학과", "데이터테크놀로지학과", null, StudentCategory.CHANGE_MAJOR);
		//then
		assertThat(user)
			.extracting("name", "major", "changeMajor", "subMajor", "studentCategory")
			.contains("테스터2", "경영학과", "데이터테크놀로지학과", null, StudentCategory.CHANGE_MAJOR);
	}


	@DisplayName("checkBeforeEntryYear 메서드 테스트")
	@Test
	void checkBeforeEntryYear() {
		assertThat(user.checkBeforeEntryYear(21)).isTrue();
		assertThat(user.checkBeforeEntryYear(19)).isFalse();
	}

	@DisplayName("checkMajor 메서드 테스트")
	@Test
	void checkMajor() {
		assertThat(user.checkMajor("경영학과")).isTrue();
		assertThat(user.checkMajor("경제학과")).isFalse();
	}

	@DisplayName("compareStudentNumber 메서드 테스트")
	@Test
	void compareStudentNumber() {
		assertThat(user.compareStudentNumber("60201000")).isTrue();
		assertThat(user.compareStudentNumber("60201001")).isFalse();
	}

	@DisplayName("matchPassword 메서드 테스트")
	@Test
	void matchPassword() {
		//given
		given(passwordEncoder.matches("password", "tester00!")).willReturn(true);
		//when //then
		user.matchPassword(passwordEncoder, "password");
	}

	@DisplayName("matchPassword 메서드 예외발생")
	@Test
	void matchWrongPassword() {
		//given
		given(passwordEncoder.matches("wrongPassword", "tester00!")).willReturn(false);
		//when //then
		assertThatThrownBy(() -> user.matchPassword(passwordEncoder, "wrongPassword"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("비밀번호가 일치하지 않습니다.");
	}
}