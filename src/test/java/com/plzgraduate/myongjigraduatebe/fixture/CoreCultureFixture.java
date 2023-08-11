package com.plzgraduate.myongjigraduatebe.fixture;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

public class CoreCultureFixture implements ArgumentsProvider {

	public static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return Stream.of(
			Arguments.arguments(HISTORY_PHILOSOPHY,핵심교양_역사와철학()),
			Arguments.arguments(SOCIETY_COMMUNITY, 핵심교양_사회와공동체()),
			Arguments.arguments(CULTURE_ART, 핵심교양_문화와예술()),
			Arguments.arguments(SCIENCE_TECHNOLOGY, 핵심교양_과학과기술())
		);
	}

	public static Set<CoreCulture> 핵심교양_역사와철학() {
		Set<CoreCulture> lectureSet = new HashSet<>();
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02110"), HISTORY_PHILOSOPHY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02111"), HISTORY_PHILOSOPHY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02112"), HISTORY_PHILOSOPHY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02140"), HISTORY_PHILOSOPHY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02158"), HISTORY_PHILOSOPHY));
		return lectureSet;
	}

	public static Set<CoreCulture> 핵심교양_사회와공동체() {
		Set<CoreCulture> lectureSet = new HashSet<>();
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02113"), SOCIETY_COMMUNITY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02114"), SOCIETY_COMMUNITY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02131"), SOCIETY_COMMUNITY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02142"), SOCIETY_COMMUNITY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02160"), SOCIETY_COMMUNITY));
		return lectureSet;
	}

	public static Set<CoreCulture> 핵심교양_문화와예술() {
		Set<CoreCulture> lectureSet = new HashSet<>();
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02128"), CULTURE_ART));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02130"), CULTURE_ART));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02132"), CULTURE_ART));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02155"), CULTURE_ART));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02156"), CULTURE_ART));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02159"), CULTURE_ART));
		return lectureSet;
	}

	public static Set<CoreCulture> 핵심교양_과학과기술() {
		Set<CoreCulture> lectureSet = new HashSet<>();
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02119"), SCIENCE_TECHNOLOGY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02120"), SCIENCE_TECHNOLOGY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02127"), SCIENCE_TECHNOLOGY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02135"), SCIENCE_TECHNOLOGY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02136"), SCIENCE_TECHNOLOGY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02138"), SCIENCE_TECHNOLOGY));
		lectureSet.add(CoreCulture.of(mockLectureMap.get("KMA02139"), SCIENCE_TECHNOLOGY));
		return lectureSet;
	}
}
