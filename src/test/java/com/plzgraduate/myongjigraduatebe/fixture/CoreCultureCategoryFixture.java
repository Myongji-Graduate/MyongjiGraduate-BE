package com.plzgraduate.myongjigraduatebe.fixture;

import static com.plzgraduate.myongjigraduatebe.fixture.CoreCultureFixture.핵심교양_과학과기술;
import static com.plzgraduate.myongjigraduatebe.fixture.CoreCultureFixture.핵심교양_문화와예술;
import static com.plzgraduate.myongjigraduatebe.fixture.CoreCultureFixture.핵심교양_사회와공동체;
import static com.plzgraduate.myongjigraduatebe.fixture.CoreCultureFixture.핵심교양_역사와철학;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.CULTURE_ART;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.HISTORY_PHILOSOPHY;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.SCIENCE_TECHNOLOGY;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.SOCIETY_COMMUNITY;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;


public class CoreCultureCategoryFixture implements ArgumentsProvider {

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return Stream.of(
			Arguments.arguments(HISTORY_PHILOSOPHY, 핵심교양_역사와철학()),
			Arguments.arguments(SOCIETY_COMMUNITY, 핵심교양_사회와공동체()),
			Arguments.arguments(CULTURE_ART, 핵심교양_문화와예술()),
			Arguments.arguments(SCIENCE_TECHNOLOGY, 핵심교양_과학과기술())
		);
	}
}
