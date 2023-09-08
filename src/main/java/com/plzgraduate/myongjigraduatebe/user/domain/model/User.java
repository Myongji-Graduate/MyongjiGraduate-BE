package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.plzgraduate.myongjigraduatebe.parsing.application.service.InvalidPdfException;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.UpdateStudentInformationCommand;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

	private final Long id;
	private String authId;
	private String password;
	private EnglishLevel englishLevel;
	private String name;
	private final String studentNumber;
	private final int entryYear;
	private String major;
	private String subMajor;
	private StudentCategory studentCategory;
	private final Instant createdAt;
	private Instant updatedAt;

	@Builder
	private User(Long id, String authId, String password, EnglishLevel englishLevel, String name, String studentNumber,
		int entryYear,
		String major, String subMajor, StudentCategory studentCategory, Instant createdAt, Instant updatedAt) {
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

	public void updateStudentInformation(UpdateStudentInformationCommand updateStudentInformationCommand) {
		this.name = updateStudentInformationCommand.getName();
		this.major = updateStudentInformationCommand.getMajor();
		this.subMajor = updateStudentInformationCommand.getSubMajor();
		this.studentCategory = updateStudentInformationCommand.getStudentCategory();
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
		if(!passwordEncoder.matches(password, this.password)) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
	}
	private static int parseEntryYearInStudentNumber(String studentNumber) {
		return Integer.parseInt(studentNumber.substring(2, 4));
	}

}
