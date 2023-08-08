package com.plzgraduate.myongjigraduatebe.fixture;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;

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
			Arguments.arguments(CHRISTIAN_A, 공통교양_16_17()),
			Arguments.arguments(CHRISTIAN_A, 공통교양_18_19()),
			Arguments.arguments(CHRISTIAN_B, 공통교양_20_21_22()),
			Arguments.arguments(CHRISTIAN_B, 공통교양_23()),
			Arguments.arguments(EXPRESSION, 공통교양_16_17()),
			Arguments.arguments(EXPRESSION, 공통교양_18_19()),
			Arguments.arguments(EXPRESSION, 공통교양_20_21_22()),
			Arguments.arguments(EXPRESSION, 공통교양_23()),
			Arguments.arguments(CAREER, 공통교양_18_19()),
			Arguments.arguments(CAREER, 공통교양_20_21_22()),
			Arguments.arguments(DIGITAL_LITERACY, 공통교양_23())
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
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02104"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02105"), EXPRESSION));
		lectureSet.add(CommonCulture.of(mockLectureMap.get("KMA02141"), DIGITAL_LITERACY));
		return lectureSet;
	}
}
