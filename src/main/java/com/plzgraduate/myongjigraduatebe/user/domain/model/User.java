package com.plzgraduate.myongjigraduatebe.user.domain.model;


import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

	private String userId;

	private String password;

	private EnglishLevel engLv;

	private String name;

	private final int studentNumber;

	private StudentInformation studentInformation;

	@Builder
	private User(String userId, String password, EnglishLevel engLv, String name, int studentNumber,
		StudentInformation studentInformation) {
		this.userId = userId;
		this.password = password;
		this.engLv = engLv;
		this.name = name;
		this.studentNumber = studentNumber;
		this.studentInformation = studentInformation;
	}

	public static User create(String userId, String password, EnglishLevel engLv, String name, int studentNumber,
		StudentInformation studentInformation) {
		return User.builder()
			.userId(userId)
			.password(password)
			.engLv(engLv)
			.name(name)
			.studentNumber(studentNumber)
			.studentInformation(studentInformation)
			.build();
	}


}
