package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.time.Instant;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

	private final Long id;
	private final String authId;
	private String password;
	private final EnglishLevel englishLevel;
	private String name;
	private final String studentNumber;
	private final int entryYear;
	private String major;
	private String changeMajor;
	private String subMajor;
	private StudentCategory studentCategory;
	private final Instant createdAt;
	private Instant updatedAt;

	@Builder
	private User(Long id, String authId, String password, EnglishLevel englishLevel, String name, String studentNumber,
		int entryYear, String major, String changeMajor, String subMajor, StudentCategory studentCategory,
		Instant createdAt, Instant updatedAt) {
		this.id = id;
		this.authId = authId;
		this.password = password;
		this.englishLevel = englishLevel;
		this.name = name;
		this.studentNumber = studentNumber;
		this.entryYear = entryYear;
		this.major = major;
		this.changeMajor = changeMajor;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;

	}


	public static User create(String authId, String password, EnglishLevel englishLevel, String studentNumber) {
		return User.builder()
			.authId(authId)
			.password(password)
			.englishLevel(englishLevel)
			.studentNumber(studentNumber)
			.entryYear(parseEntryYearInStudentNumber(studentNumber))
			.build();
	}

	public void updateStudentInformation(String name, String major, String changeMajor, String subMajor,
		StudentCategory studentCategory) {
		this.name = name;
		this.major = major;
		this.changeMajor = changeMajor;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;
	}

	public boolean checkBeforeEntryYear(int entryYear) {
		return this.entryYear < entryYear;
	}

	public boolean checkMajor(String major) {
		return this.major.equals(major);
	}

	public boolean compareStudentNumber(String studentNumber) {
		return this.studentNumber.equals(studentNumber);
	}

	public void matchPassword(PasswordEncoder passwordEncoder, String password) {
		if (!passwordEncoder.matches(password, this.password)) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
	}

	public String getEncryptedAuthId() {
		return authId.replace(authId.substring(authId.length() - 3), "***");
	}

	public void resetPassword(String newPassword) {
		this.password = newPassword;
	}

	public boolean isMyAuthId(String authId) {
		return this.authId.equals(authId);
	}

	private static int parseEntryYearInStudentNumber(String studentNumber) {
		return Integer.parseInt(studentNumber.substring(2, 4));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User)o;
		return Objects.equals(authId, user.authId) && Objects.equals(studentNumber, user.studentNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(authId, studentNumber);
	}
}
