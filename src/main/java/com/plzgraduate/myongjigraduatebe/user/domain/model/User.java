package com.plzgraduate.myongjigraduatebe.user.domain.model;


import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

	private String userId;
	private String password;
	private EnglishLevel englishLevel;
	private String name;
	private final int studentNumber;
	private StudentInformation studentInformation;

	@Builder
	private User(String userId, String password, EnglishLevel englishLevel, String name, int studentNumber,
		StudentInformation studentInformation) {
		this.userId = userId;
		this.password = password;
		this.englishLevel = englishLevel;
		this.name = name;
		this.studentNumber = studentNumber;
		this.studentInformation = studentInformation;
	}

	public static User create(String userId, String password, EnglishLevel englishLevel, String name, int studentNumber,
		StudentInformation studentInformation) {
		return User.builder()
			.userId(userId)
			.password(password)
			.englishLevel(englishLevel)
			.name(name)
			.studentNumber(studentNumber)
			.studentInformation(studentInformation)
			.build();
	}


}
