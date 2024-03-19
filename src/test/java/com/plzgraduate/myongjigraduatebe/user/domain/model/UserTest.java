package com.plzgraduate.myongjigraduatebe.user.domain.model;

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
			.primaryMajor("경영학과")
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
		user.updateStudentInformation("테스터2", "경영학과", "데이터테크놀로지학과", null, null, StudentCategory.CHANGE_MAJOR);
		//then
		assertThat(user)
			.extracting("name", "primaryMajor", "changeMajor", "subMajor", "dualMajor","studentCategory")
			.contains("테스터2", "경영학과", "데이터테크놀로지학과", null, null, StudentCategory.CHANGE_MAJOR);
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

	@DisplayName("유저의 암호화된 로그인 아이디(뒷 세자리 *** 대체)를 반환한다.")
	@Test
	void getEncryptedAuthId() {
		//given
		String authId = "testAuthId";
		User user = User.builder()
			.authId(authId).build();

		//when
		String encryptedAuthId = user.getEncryptedAuthId();

		//then
		assertThat(encryptedAuthId.substring(0, encryptedAuthId.length() - 4))
			.isEqualTo(authId.substring(0, authId.length() - 4));
		assertThat(encryptedAuthId.substring(encryptedAuthId.length() - 3)).isEqualTo(
			"***");
	}

	@DisplayName("유저의 패스워드를 초기화한다.")
	@Test
	void resetPassword() {
	    //given
		String beforePassword = "before";
		String afterPassword = "after";
		User user = User.builder()
			.password(beforePassword).build();

		//when
		user.resetPassword(afterPassword);

	    //then
		assertThat(user.getPassword()).isEqualTo(afterPassword);
	}

	@DisplayName("유저의 로그인 아이디와 같을 경우 true를 반환한다.")
	@Test
	void isMyAuthId() {
	    //given
		String authId = "testAuthId";
		User user = User.builder()
			.authId(authId).build();

		//when
		boolean result = user.isMyAuthId(authId);

		//then
		assertThat(result).isTrue();
	}

	@DisplayName("유저의 로그인 아이디와 다를 경우 false를 반환한다.")
	@Test
	void isNotMyAuthId() {
		//given
		String authId = "testAuthId";
		User user = User.builder()
			.authId("userAuthId").build();

		//when
		boolean result = user.isMyAuthId(authId);

		//then
		assertThat(result).isFalse();
	}
}
