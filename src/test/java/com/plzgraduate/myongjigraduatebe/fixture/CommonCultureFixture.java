package com.plzgraduate.myongjigraduatebe.fixture;

import static com.plzgraduate.myongjigraduatebe.fixture.UserFixture.*;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CAREER;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_A;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.CHRISTIAN_B;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.DIGITAL_LITERACY;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.ENGLISH;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.EXPRESSION;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public class CommonCultureFixture implements ArgumentsProvider {

	public static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return Stream.of(
			Arguments.arguments(영문학과_16학번(), 공통교양_16_17()),
			Arguments.arguments(영문학과_18학번(), 공통교양_18_19()),
			Arguments.arguments(행정학과_21학번(), 공통교양_20_21_22()),
			Arguments.arguments(경영학과_23학번(), 공통교양_23())
		);
	}

	public static Set<CommonCulture> 공통교양_16_17() {
		Set<CommonCulture> lectureSet = new HashSet<>();
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02100"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA00100"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA00101"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02102"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02103"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02122"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02106"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02107"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02108"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02109"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02104"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02105"), EXPRESSION));
		return lectureSet;
	}

	public static Set<CommonCulture> 공통교양_18_19() {
		Set<CommonCulture> lectureSet = new HashSet<>();
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02100"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA00100"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA00101"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02102"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02103"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02122"), CHRISTIAN_A));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02106"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02107"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02108"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02109"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02104"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02105"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02137"), CAREER));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02141"), CAREER));
		return lectureSet;
	}

	public static Set<CommonCulture> 공통교양_20_21_22() {
		Set<CommonCulture> lectureSet = new HashSet<>();
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA00101"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02102"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02103"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02122"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02123"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02124"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02125"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02126"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02104"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02105"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02141"), CAREER));
		return lectureSet;
	}

	public static Set<CommonCulture> 공통교양_23() {
		Set<CommonCulture> lectureSet = new HashSet<>();
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA00101"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02102"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02103"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02122"), CHRISTIAN_B));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02123"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02124"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02125"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02126"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02104"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02105"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02141"), DIGITAL_LITERACY));
		return lectureSet;
	}

	public static Set<CommonCulture> 영어레벨_Basic() {
		Set<CommonCulture> lectureSet = new HashSet<>();
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMP02126"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02106"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02107"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02108"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02109"), ENGLISH));
		return lectureSet;
	}

	public static Set<CommonCulture> 영어레벨_12() {
		Set<CommonCulture> lectureSet = new HashSet<>();
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02106"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02107"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02108"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02109"), ENGLISH));
		return lectureSet;
	}

	public static Set<CommonCulture> 영어레벨_34() {
		Set<CommonCulture> lectureSet = new HashSet<>();
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02123"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02124"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02125"), ENGLISH));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02126"), ENGLISH));
		return lectureSet;
	}
}
