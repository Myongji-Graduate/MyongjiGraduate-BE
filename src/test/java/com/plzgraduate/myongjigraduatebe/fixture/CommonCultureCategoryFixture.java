package com.plzgraduate.myongjigraduatebe.fixture;

import static com.plzgraduate.myongjigraduatebe.fixture.CommonCultureFixture.*;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CommonCultureCategory.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


public class CommonCultureCategoryFixture implements ArgumentsProvider {

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
}
