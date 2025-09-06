package com.plzgraduate.myongjigraduatebe.fixture;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.KoreanLevel;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public class UserFixture {


	public static User 영문학과_16학번() {
		return createUser(
			"mj01",
			"1234",
			EnglishLevel.ENG12,
			KoreanLevel.FREE,
			"김영문",
			"60161001",
			16,
			"영어영문학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 영문학과_18학번() {
		return createUser(
			"mj01",
			"1234",
			EnglishLevel.ENG12,
			KoreanLevel.FREE,
			"김영문",
			"60181001",
			18,
			"영어영문학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 철학과_20학번() {
		return createUser(
			"mj51",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김철학",
			"60201011",
			20,
			"철학학과",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 행정학과_21학번() {
		return createUser(
			"mj12",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김행정",
			"60211012",
			21,
			"행정학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_19학번_ENG12() {
		return createUser(
			"mj21",
			"1234",
			EnglishLevel.ENG12,
			KoreanLevel.FREE,
			"김경영",
			"60191021",
			19,
			"경영학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_19학번_ENG34() {
		return createUser(
			"mj21",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김경영",
			"60191021",
			19,
			"경영학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_19학번_영어_면제() {
		return createUser(
			"mj21",
			"1234",
			EnglishLevel.FREE,
			KoreanLevel.FREE,
			"김경영",
			"60191021",
			19,
			"경영학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_22학번() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김경영",
			"60221022",
			22,
			"경영학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_23학번() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김경영",
			"60231022",
			23,
			"경영학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_23학번_국제통상학과_부전공() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김경영",
			"60231022",
			23,
			"경영학과",
			"국제통상학전공",
			null,
			StudentCategory.SUB_MAJOR,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 국제통상학과_19학번() {
		return createUser(
			"mj31",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김국통",
			"60192021",
			19,
			"국제통상학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 데이테크놀로지전공_16학번() {
		return createUser(
			"mj1001",
			"1234",
			EnglishLevel.ENG12,
			KoreanLevel.FREE,
			"정데테",
			"60161666",
			16,
			"데이터사이언스전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 데이테크놀로지전공_16학번_Eng34() {
		return createUser(
			"mj1001",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"정데테",
			"60161666",
			16,
			"데이터사이언스전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 데이테크놀로지전공_18학번_Basic_Eng() {
		return createUser(
			"mj1001",
			"1234",
			EnglishLevel.BASIC,
			KoreanLevel.FREE,
			"정데테",
			"60181666",
			18,
			"데이터사이언스전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 데이테크놀로지전공_18학번() {
		return createUser(
			"mj1003",
			"1234",
			EnglishLevel.ENG12,
			KoreanLevel.FREE,
			"정데테",
			"60181666",
			18,
			"데이터사이언스전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경제학과_20학번_편입() {
		return createUser(
			"mj1003",
			"1234",
			EnglishLevel.FREE,
			KoreanLevel.FREE,
			"최편입",
			"60191666",
			20,
			"경제학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 응용소프트웨어전공_17학번() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김응용",
			"60171022",
			17,
			"응용소프트웨어전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 응용소프트웨어전공_19학번() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김응용",
			"60191022",
			19,
			"응용소프트웨어전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 응용소프트웨어전공_19학번_영어_면제() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.FREE,
			KoreanLevel.FREE,
			"김응용",
			"60191022",
			19,
			"응용소프트웨어전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 데이터테크놀로지전공_19학번() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김응용",
			"60191022",
			19,
			"데이터테사이언스전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 디지털콘텐츠디자인학과_19학번() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김응용",
			"60191022",
			19,
			"디지털콘텐츠디자인학과",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 디지털콘텐츠디자인학과_23학번() {
		return createUser(
			"mj22",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김응용",
			"60231022",
			23,
			"디지털콘텐츠디자인학과",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 데이터테크놀로지전공_21학번_외국인학생_KOR12() {
		return createUser(
			"fs20",
			"1234",
			EnglishLevel.FREE,
			KoreanLevel.KOR12,
			"김외국",
			"60211022",
			23,
			"데이터사이언스전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 데이터테크놀로지전공_21학번_외국인학생_KOR34() {
		return createUser(
				"fs20",
				"1234",
				EnglishLevel.FREE,
				KoreanLevel.KOR12,
				"김외국",
				"60211022",
				23,
				"데이터사이언스전공",
				null,
				null,
				StudentCategory.NORMAL,
				"0/0/0/0",
				"0/0/0/0/0/0/0/0"
		);
	}

	public static User 영문학과_18학번_교환_학문기초3학점() {
		return createUser(
			"mj01",
			"1234",
			EnglishLevel.ENG12,
			KoreanLevel.FREE,
			"김영문",
			"60181001",
			18,
			"영어영문학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"3/0/0/0/0/0/0/0"
		);
	}


	public static User 경제학과_18학번_교환_자율학점3학점() {
		return createUser(
			"mj01",
			"1234",
			EnglishLevel.ENG12,
			KoreanLevel.FREE,
			"김경제",
			"60181001",
			18,
			"경제학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_19학번_ENG34_교환학생_일반학점_3() {
		return createUser(
			"mj21",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김경영",
			"60191021",
			19,
			"경영학전공",
			null,
			null,
			StudentCategory.NORMAL,
			"0/0/0/0",
			"0/3/0/0/0/0/0/0"
		);
	}

	public static User 응용_경영_복전_19학번_교환학생() {
		return createUser(
			"mj21",
			"1234",
			EnglishLevel.ENG34,
			KoreanLevel.FREE,
			"김융경",
			"60191021",
			19,
			"응용소프트웨어전공",
			null,
			"경영학과",
			StudentCategory.DUAL_MAJOR,
			"0/0/0/0",
			"0/0/0/0/15/0/0/0"
		);
	}

	public static User 글로벌비즈니스학전공_25학번() {
		return createUser(
				"mj22",
				"1234",
				EnglishLevel.ENG34,
				KoreanLevel.FREE,
				"김글로",
				"60251022",
				25,
				"글로벌비즈니스학전공",
				null,
				null,
				StudentCategory.NORMAL,
				"0/0/0/0",
				"0/0/0/0/0/0/0/0"
		);
	}

	public static User 경영학과_25학번() {
		return createUser(
				"mj22",
				"1234",
				EnglishLevel.ENG34,
				KoreanLevel.FREE,
				"김경영",
				"60251022",
				25,
				"경영학전공",
				null,
				null,
				StudentCategory.NORMAL,
				"0/0/0/0",
				"0/0/0/0/0/0/0/0"
		);
	}

	public static User 한국어_레벨_NULL() {
		return createUser(
				"mjTest",
				"1234",
				EnglishLevel.ENG12,
				null,
				"김테스트",
				"60180001",
				18,
				"테스트학과",
				null,
				null,
				StudentCategory.NORMAL,
				"0/0/0/0",
				"0/0/0/0/0/0/0/0"
		);
	}

	public static User 한국어_레벨_FREE() {
		return createUser(
				"mjTest2",
				"1234",
				EnglishLevel.ENG12,
				KoreanLevel.FREE,
				"김테스트",
				"60180002",
				18,
				"테스트학과",
				null,
				null,
				StudentCategory.NORMAL,
				"0/0/0/0",
				"0/0/0/0/0/0/0/0"
		);
	}

	public static User 교환공통교양3학점_20학번() {
		return createUser(
				"auth",
				"pw",
				EnglishLevel.ENG12,
				KoreanLevel.FREE,
				"테스트",
				"20201234",
				20,
				"테스트전공",
				null,
				null,
				StudentCategory.NORMAL,
				"0/0/0/0",                  // transferCredit
				"3/0/0/0/0/0/0/0/0"         // exchangeCredit: 9필드, commonCulture=3
		);
	}

	public static User createUser(
		String authId,
		String password,
		EnglishLevel englishLevel,
		KoreanLevel koreanLevel,
		String name,
		String studentNumber,
		int entryYear,
		String major,
		String dualMajor,
		String subMajor,
		StudentCategory studentCategory,
		String transferCredit,
		String exchangeCredit
	) {
		return User.builder()
			.id(1L)
			.authId(authId)
			.password(password)
			.englishLevel(englishLevel)
			.koreanLevel(koreanLevel)
			.name(name)
			.studentNumber(studentNumber)
			.entryYear(entryYear)
			.primaryMajor(major)
			.dualMajor(dualMajor)
			.subMajor(subMajor)
			.studentCategory(studentCategory)
			.transferCredit(TransferCredit.from(transferCredit))
			.exchangeCredit(ExchangeCredit.from(exchangeCredit))
			.build();
	}
}
