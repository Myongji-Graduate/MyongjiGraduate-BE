package com.plzgraduate.myongjigraduatebe.fixture;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public class UserFixture {

	public static User 영문학과_16학번() {
		return createUser("mj01", "1234", EnglishLevel.ENG12, "김영문", "60161001", 16, "영어영문", null, StudentCategory.NORMAL);
	}

	public static User 영문학과_18학번() {
		return createUser("mj01", "1234", EnglishLevel.ENG12, "김영문", "60181001", 18, "영어영문", null, StudentCategory.NORMAL);
	}

	public static User 철학과_20학번() {
		return createUser("mj51", "1234", EnglishLevel.ENG34, "김철학", "60201011", 20, "철학", null, StudentCategory.NORMAL);
	}

	public static User 행정학과_18학번() {
		return createUser("mj11", "1234", EnglishLevel.ENG34, "김행정", "60181011", 18, "행정", null, StudentCategory.NORMAL);
	}

	public static User 행정학과_21학번() {
		return createUser("mj12", "1234", EnglishLevel.ENG34, "김행정", "60211012", 21, "행정", null, StudentCategory.NORMAL);
	}

	public static User 경영학과_19학번() {
		return createUser("mj21", "1234", EnglishLevel.ENG34, "김경영", "60191021", 19, "경영", null, StudentCategory.NORMAL);
	}

	public static User 경영학과_19학번_영어_면제() {
		return createUser("mj21", "1234", EnglishLevel.FREE, "김경영", "60191021", 19, "경영", null, StudentCategory.NORMAL);
	}

	public static User 경영학과_22학번() {
		return createUser("mj22", "1234", EnglishLevel.ENG34, "김경영", "60221022", 22, "경영", null, StudentCategory.NORMAL);
	}

	public static User 경영학과_23학번() {
		return createUser("mj22", "1234", EnglishLevel.ENG34, "김경영", "60231022", 23, "경영", null, StudentCategory.NORMAL);
	}

	public static User 국제통상학과_19학번() {
		return createUser("mj31", "1234", EnglishLevel.ENG34, "김국통", "60192021", 19, "국제통상", null, StudentCategory.NORMAL);
	}

	public static User 데이테크놀로지학과_16학번() {
		return createUser("mj1001", "1234", EnglishLevel.ENG12, "정데테", "60161666", 16, "데이터테크놀로지", null, StudentCategory.NORMAL);
	}

	public static User 데이테크놀로지학과_18학번() {
		return createUser("mj1003", "1234", EnglishLevel.ENG12, "정데테", "60181666", 18, "데이터테크놀로지", null, StudentCategory.NORMAL);
	}

	public static User 응용소프트웨어학과_17학번() {
		return createUser("mj22", "1234", EnglishLevel.ENG34, "김응용", "60171022", 17, "응용소프트웨어", null, StudentCategory.NORMAL);
	}

	public static User 응용소프트웨어학과_19학번() {
		return createUser("mj22", "1234", EnglishLevel.ENG34, "김응용", "60191022", 19, "응용소프트웨어", null, StudentCategory.NORMAL);
	}

	public static User 데이터테크놀로지학과_19학번() {
		return createUser("mj22", "1234", EnglishLevel.ENG34, "김응용", "60191022", 19, "데이터테크놀로지", null, StudentCategory.NORMAL);
	}

	public static User 디지털콘텐츠디자인학과_19학번() {
		return createUser("mj22", "1234", EnglishLevel.ENG34, "김응용", "60191022", 19, "디지털콘텐츠디자인", null, StudentCategory.NORMAL);
	}

	public static User 디지털콘텐츠디자인학과_23학번() {
		return createUser("mj22", "1234", EnglishLevel.ENG34, "김응용", "60231022", 23, "디지털콘텐츠디자인", null, StudentCategory.NORMAL);
	}

	public static User createUser(String authId, String password, EnglishLevel englishLevel, String name, String studentNumber,
		int entryYear, String major, String subMajor, StudentCategory studentCategory) {
		return User.builder()
			.authId(authId)
			.password(password)
			.englishLevel(englishLevel)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.major(major)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.build();
	}

}
