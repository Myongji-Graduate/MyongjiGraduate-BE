package com.plzgraduate.myongjigraduatebe.user.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

	private final Long id;
	private String authId;
	private String password;
	private EnglishLevel englishLevel;
	private final String name;
	private final String studentNumber;
	private final int entryYear;
	private final String major;
	private final String subMajor;
	private final StudentCategory studentCategory;

	@Builder
	private User(Long id, String authId, String password, EnglishLevel englishLevel, String name, String studentNumber, int entryYear,
		String major, String subMajor, StudentCategory studentCategory) {
		this.id = id;
		this.authId = authId;
		this.password = password;
		this.englishLevel = englishLevel;
		this.name = name;
		this.studentNumber = studentNumber;
		this.entryYear = entryYear;
		this.major = major;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;

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

	public boolean checkBeforeEntryYear(int entryYear) {
		return this.entryYear < entryYear;
	}

	public boolean checkMajor(String major) {
		return this.major.equals(major);
	}

	private static int parseEntryYearInStudentNumber(String studentNumber) {
		return Integer.parseInt(studentNumber.substring(2,4));
	}


}
