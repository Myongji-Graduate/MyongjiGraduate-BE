package com.plzgraduate.myongjigraduatebe.user.domain.model;

import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType.DUAL;
import static com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType.PRIMARY;

import java.time.Instant;
import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;

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
	private String primaryMajor;
	private String subMajor;
	private String dualMajor;
	private StudentCategory studentCategory;
	private int totalCredit;
	private double takenCredit;
	private boolean graduated;
	private final Instant createdAt;
	private Instant updatedAt;

	@Builder
	private User(Long id, String authId, String password, EnglishLevel englishLevel, String name, String studentNumber,
		int entryYear, String primaryMajor, String subMajor, String dualMajor, StudentCategory studentCategory,
		int totalCredit, double takenCredit, boolean graduated, Instant createdAt, Instant updatedAt) {
		this.id = id;
		this.authId = authId;
		this.password = password;
		this.englishLevel = englishLevel;
		this.name = name;
		this.studentNumber = studentNumber;
		this.entryYear = entryYear;
		this.primaryMajor = primaryMajor;
		this.subMajor = subMajor;
		this.dualMajor = dualMajor;
		this.studentCategory = studentCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduated = graduated;
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
			.totalCredit(0)
			.takenCredit(0)
			.graduated(false)
			.build();
	}

	public void updateStudentInformation(String name, String major, String dualMajor, String subMajor,
		StudentCategory studentCategory, int totalCredit, double takenCredit, boolean graduate) {
		this.name = name;
		this.primaryMajor = major;
		this.dualMajor = dualMajor;
		this.subMajor = subMajor;
		this.studentCategory = studentCategory;
		this.totalCredit = totalCredit;
		this.takenCredit = takenCredit;
		this.graduated = graduate;
	}

	public boolean checkBeforeEntryYear(int entryYear) {
		return this.entryYear < entryYear;
	}

	public boolean checkMajor(String major) {
		return this.primaryMajor.equals(major);
	}

	public boolean compareStudentNumber(String studentNumber) {
		return this.studentNumber.equals(studentNumber);
	}

	public boolean matchPassword(PasswordEncoder passwordEncoder, String password) {
		return passwordEncoder.matches(password, this.password);
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

	public String getMajorByMajorType(MajorType majorType) {
		if (majorType == PRIMARY) {
			return primaryMajor;
		} else if (majorType == DUAL) {
			return dualMajor;
		}
		return subMajor;
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
