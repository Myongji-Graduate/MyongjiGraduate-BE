package com.plzgraduate.myongjigraduatebe.fixture;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.CULTURE_ART;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.HISTORY_PHILOSOPHY;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.SCIENCE_TECHNOLOGY;
import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCultureCategory.SOCIETY_COMMUNITY;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.CoreCulture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoreCultureFixture {

	public static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	public static Set<CoreCulture> getAllCoreCulture() {
		Set<CoreCulture> coreCultures = new HashSet<>();
		coreCultures.addAll(핵심교양_역사와철학());
		coreCultures.addAll(핵심교양_사회와공동체());
		coreCultures.addAll(핵심교양_문화와예술());
		coreCultures.addAll(핵심교양_과학과기술());
		return coreCultures;
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
