package com.plzgraduate.myongjigraduatebe.fixture;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.BasicAcademicalCultureLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class
BasicAcademicalLectureFixture {

	public static final String 인문대 = "인문대";
	public static final String 사회과학대 = "사회과학대";
	public static final String 경영대 = "경영대";
	public static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	public static Set<BasicAcademicalCultureLecture> 인문대_학문기초교양() {
		Set<BasicAcademicalCultureLecture> lectureSet = new HashSet<>();
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02127"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02119"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02120"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02128"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02122"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02121"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02125"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02123"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02124"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02126"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02141"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02142"), 인문대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMH02101"), 인문대));
		return lectureSet;
	}

	public static Set<BasicAcademicalCultureLecture> 사회과학대_학문기초교양() {
		Set<BasicAcademicalCultureLecture> lectureSet = new HashSet<>();
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02109"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02123"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02103"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02103"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02116"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02121"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02145"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02168"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02171"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMM02106"), 사회과학대));

		//2023-1 이후 수강부터 가능
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02102"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02108"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02140"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02186"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02113"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02104"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02114"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02115"), 사회과학대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMB02163"), 사회과학대));
		return lectureSet;
	}

	public static Set<BasicAcademicalCultureLecture> 경영대_학문기초교양() {
		Set<BasicAcademicalCultureLecture> lectureSet = new HashSet<>();
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02123"), 경영대));
		lectureSet.add(BasicAcademicalCultureLecture.of(mockLectureMap.get("KMD02107"), 경영대));
		return lectureSet;
	}
}
