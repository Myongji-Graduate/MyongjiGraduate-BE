package com.plzgraduate.myongjigraduatebe.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
		user.updateStudentInformation("테스터2", "경영학과", null, null, StudentCategory.CHANGE_MAJOR, 134,
			120.5, true);
		//then
		assertThat(user)
			.extracting("name", "primaryMajor", "subMajor", "dualMajor", "studentCategory",
				"totalCredit", "takenCredit", "graduated")
			.contains("테스터2", "경영학과", null, null, StudentCategory.CHANGE_MAJOR, 134, 120.5, true);
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

		//when
		boolean result = user.matchPassword(passwordEncoder, "wrongPassword");

		// then
		assertThat(result).isFalse();
	}

	@DisplayName("유저의 암호화된 로그인 아이디(뒷 세자리 *** 대체)를 반환한다.")
	@Test
	void getEncryptedAuthId() {
		//given
		String authId = "testAuthId";
		User user = User.builder()
			.authId(authId)
			.build();

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
			.password(beforePassword)
			.build();

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
			.authId(authId)
			.build();

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
			.authId("userAuthId")
			.build();

		//when
		boolean result = user.isMyAuthId(authId);

		//then
		assertThat(result).isFalse();
	}

	@DisplayName("MajorType별 사용자의 전공을 반환하는지 확인한다.")
	@CsvSource({"PRIMARY, 융합소프트웨어부", "DUAL, 경영학과", "SUB, 영문학과"})
	@ParameterizedTest
	void getMajorByMajorType(MajorType majorType, String major) {
		//given
		User dualMajorUser = User.builder()
			.id(1L)
			.primaryMajor("융합소프트웨어부")
			.dualMajor("경영학과")
			.subMajor("영문학과")
			.build();

		//when
		String majorByMajorType = dualMajorUser.getMajorByMajorType(majorType);

		//then
		assertThat(majorByMajorType).isEqualTo(major);
	}
}
