package com.plzgraduate.myongjigraduatebe.fixture;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public class UserFixture {

	public static User 영문학과_16학번() {
		StudentInformation studentInformation = StudentInformation.of(16, "영어영문학과", StudentCategory.NORMAL);
		return User.create("mj01", "1234", EnglishLevel.ENG12, "김영문", 60161001, studentInformation);
	}

	public static User 영문학과_18학번() {
		StudentInformation studentInformation = StudentInformation.of(18, "영어영문학과", StudentCategory.NORMAL);
		return User.create("mj01", "1234", EnglishLevel.ENG12, "김영문", 60181001, studentInformation);
	}

	public static User 철학과_20학번() {
		StudentInformation studentInformation = StudentInformation.of(20, "철학과", StudentCategory.NORMAL);
		return User.create("mj51", "1234", EnglishLevel.ENG34, "김철학", 60201011, studentInformation);
	}

	public static User 행정학과_18학번() {
		StudentInformation studentInformation = StudentInformation.of(18, "행정학과", StudentCategory.NORMAL);
		return User.create("mj11", "1234", EnglishLevel.ENG34, "김행정", 60181011, studentInformation);
	}

	public static User 행정학과_21학번() {
		StudentInformation studentInformation = StudentInformation.of(21, "행정학과", StudentCategory.NORMAL);
		return User.create("mj12", "1234", EnglishLevel.ENG34, "김행정", 60211012, studentInformation);
	}

	public static User 경영학과_19학번() {
		StudentInformation studentInformation = StudentInformation.of(19, "경영학과", StudentCategory.NORMAL);
		return User.create("mj21", "1234", EnglishLevel.ENG34, "김경영", 60191021, studentInformation);
	}

	public static User 경영학과_22학번() {
		StudentInformation studentInformation = StudentInformation.of(22, "경영학과", StudentCategory.NORMAL);
		return User.create("mj22", "1234", EnglishLevel.ENG34, "김경영", 60221022, studentInformation);
	}

	public static User 경영학과_23학번() {
		StudentInformation studentInformation = StudentInformation.of(23, "경영학과", StudentCategory.NORMAL);
		return User.create("mj22", "1234", EnglishLevel.ENG34, "김경영", 60231022, studentInformation);
	}

	public static User 국제통상학과_19학번() {
		StudentInformation studentInformation = StudentInformation.of(19, "국제통상학과", StudentCategory.NORMAL);
		return User.create("mj31", "1234", EnglishLevel.ENG34, "김국통", 60192021, studentInformation);
	}

	public static User 데이테크놀로지학과_16학번() {
		StudentInformation studentInformation = StudentInformation.of(16, "데이터테크놀로지학과", StudentCategory.NORMAL);
		return User.create("mj1001", "1234", EnglishLevel.ENG12, "정데테", 60161666, studentInformation);
	}

	public static User 데이테크놀로지학과_18학번() {
		StudentInformation studentInformation = StudentInformation.of(18, "데이터테크놀로지학과", StudentCategory.NORMAL);
		return User.create("mj1003", "1234", EnglishLevel.ENG12, "정데테", 60181666, studentInformation);
	}
}
