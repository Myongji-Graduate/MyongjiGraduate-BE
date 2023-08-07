package com.plzgraduate.myongjigraduatebe.fixture;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public class UserFixture {

	public static User 영문학과_18학번() {
		StudentInformation studentInformation = StudentInformation.of(18, "영어영문학과", StudentCategory.NORMAL);
		return User.create("mj01", "1234", EnglishLevel.ENG12, "김영문", 60181001, studentInformation);
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

}
