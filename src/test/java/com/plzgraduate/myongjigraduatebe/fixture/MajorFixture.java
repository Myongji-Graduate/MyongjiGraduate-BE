package com.plzgraduate.myongjigraduatebe.fixture;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MajorFixture {

	public static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();
	private static final String 국제통상학과 = "국제통상학과";
	private static final String 데이터테크놀로지전공 = "데이터테크놀로지전공";


	public static Set<MajorLecture> 국제통상_전공() {
		Set<MajorLecture> lectureSet = new HashSet<>();
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01128"), 국제통상학과, 1, 16, 99)); //국제통상원론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HBX01129"), 국제통상학과, 1, 16, 99)); //글로벌경영전략
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01127"), 국제통상학과, 1, 16, 99)); //국제경영학
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01104"), 국제통상학과, 1, 16, 99)); //회계원리
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01106"), 국제통상학과, 1, 16, 99)); //마켓팅원론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01105"), 국제통상학과, 1, 16, 99)); //재무관리원론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01113"), 국제통상학과, 1, 16, 99)); //인적자원관리
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01114"), 국제통상학과, 1, 16, 99)); //생산운영관리
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBX01143"), 국제통상학과, 1, 16, 99)); //운영관리

		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HBX01142"), 국제통상학과, 0, 16, 99)); //창업과경영캡스톤
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBW01202"), 국제통상학과, 0, 16, 99)); //국제금융론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HBW01203"), 국제통상학과, 0, 16, 99)); //파생금융상품론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBW01309"), 국제통상학과, 0, 16, 99)); //국제마케팅
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBW01401"), 국제통상학과, 0, 16, 99)); //외환론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HBV01301"), 국제통상학과, 0, 16, 99)); //전자상거래
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04220"), 국제통상학과, 0, 16, 99)); //국제통상법
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04236"), 국제통상학과, 0, 16, 99)); //무역실무
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04310"), 국제통상학과, 0, 16, 99)); //무역영어
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04312"), 국제통상학과, 0, 16, 99)); //신흥시장연구
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04320"), 국제통상학과, 0, 16, 99)); //국제통상정책론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04322"), 국제통상학과, 0, 16, 99)); //국제협상전략
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04338"), 국제통상학과, 0, 16, 99)); //경제통합론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04343"), 국제통상학과, 0, 16, 99)); //해외시장조사론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04346"), 국제통상학과, 0, 16, 99)); //글로벌혁신연구
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04349"), 국제통상학과, 0, 16, 99)); //국제무역연구
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04350"), 국제통상학과, 0, 16, 99)); //EU통상론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04351"), 국제통상학과, 0, 16, 99)); //국제물류론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04395"), 국제통상학과, 0, 16, 99)); //개방경제론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04397"), 국제통상학과, 0, 16, 99)); //국제통상취업세미나
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04398"), 국제통상학과, 0, 16, 99)); //국제개발협력론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04426"), 국제통상학과, 0, 16, 99)); //다국적기업론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04432"), 국제통상학과, 0, 16, 99)); //해상보험론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04444"), 국제통상학과, 0, 16, 99)); //글로벌경영전략사례연구
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04450"), 국제통상학과, 0, 16, 99)); //국제유통경영론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04455"), 국제통상학과, 0, 16, 99)); //무역과개발협력
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04456"), 국제통상학과, 0, 16, 99)); //미국경제론
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04457"), 국제통상학과, 0, 16, 99)); //WTO연구
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04461"), 국제통상학과, 0, 16, 99)); //국제통상세미나
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04463"), 국제통상학과, 0, 16, 99)); //국제경영세미나
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HCC04495"), 국제통상학과, 0, 16, 99)); //국제물류실무
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HCC04496"), 국제통상학과, 0, 16, 99)); //글로벌전략계획
		return lectureSet;
	}

	public static Set<MajorLecture> 데이터테크놀로지_전공() {
		Set<MajorLecture> lectureSet = new HashSet<>();
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HEB01102"), 데이터테크놀로지전공, 1, 16, 99)); //기초프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HEB01104"), 데이터테크놀로지전공, 1, 16, 99)); //객체지향프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HEB01105"), 데이터테크놀로지전공, 1, 16, 99)); //기초프로그래밍2
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01201"), 데이터테크놀로지전공, 1, 16, 99)); //자료구조
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01202"), 데이터테크놀로지전공, 1, 16, 99)); //R통계분석
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01203"), 데이터테크놀로지전공, 1, 16, 99)); //데이터베이스
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01206"), 데이터테크놀로지전공, 1, 16, 16)); //기초웹프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HEB01202"), 데이터테크놀로지전공, 1, 16, 16)); //기초웹프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HEB01201"), 데이터테크놀로지전공, 1, 16, 16)); //기초웹프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01307"), 데이터테크놀로지전공, 1, 16, 99)); //알고리즘
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01301"), 데이터테크놀로지전공, 1, 16, 99)); //소프트웨어공학
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01303"), 데이터테크놀로지전공, 1, 16, 99)); //운영체제
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01306"), 데이터테크놀로지전공, 1, 16, 99)); //빅데이터프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01308"), 데이터테크놀로지전공, 1, 16, 16)); //UX디자인
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01317"), 데이터테크놀로지전공, 1, 16, 16)); //데이터베이스프로젝트
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01302"), 데이터테크놀로지전공, 1, 16, 16)); //데이터베이스프로그래처
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01315"), 데이터테크놀로지전공, 1, 17, 99)); //인공지능
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01413"), 데이터테크놀로지전공, 1, 17, 99)); //캡스톤디자인
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01401"), 데이터테크놀로지전공, 1, 17, 99)); //캡스톤디자인1
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01402"), 데이터테크놀로지전공, 1, 17, 99)); //캡스톤디자인2

		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01102"), 데이터테크놀로지전공, 0, 16, 99)); //데이터사이언스개론
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01103"), 데이터테크놀로지전공, 0, 16, 99)); //소프트웨어인턴십
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01204"), 데이터테크놀로지전공, 0, 16, 99)); //객체지향디자인패턴
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01205"), 데이터테크놀로지전공, 0, 16, 99)); //통계적데이터분석
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01207"), 데이터테크놀로지전공, 0, 16, 99)); //융합심화R통계분석
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01208"), 데이터테크놀로지전공, 0, 16, 99)); //융합데이터베이스
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01304"), 데이터테크놀로지전공, 0, 16, 99)); //고급서버프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01305"), 데이터테크놀로지전공, 0, 16, 99)); //인터랙티브인포그래픽
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01309"), 데이터테크놀로지전공, 0, 16, 99)); //컴퓨터아키텍처
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01310"), 데이터테크놀로지전공, 0, 16, 99)); //모바일컴퓨팅
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01311"), 데이터테크놀로지전공, 0, 16, 99)); //자기주도학습
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01312"), 데이터테크놀로지전공, 0, 16, 99)); //소프트웨어세미나
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01313"), 데이터테크놀로지전공, 0, 16, 99)); //컴퓨터통신
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01314"), 데이터테크놀로지전공, 0, 16, 99)); //게임프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01315"), 데이터테크놀로지전공, 0, 16, 99)); //인공지능
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01316"), 데이터테크놀로지전공, 0, 16, 99)); //고급웹프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01318"), 데이터테크놀로지전공, 0, 16, 99)); //모바일컴퓨팅
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01403"), 데이터테크놀로지전공, 0, 16, 99)); //블록체인기초
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01404"), 데이터테크놀로지전공, 0, 16, 99)); //빅데이터기술특론1
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01405"), 데이터테크놀로지전공, 0, 16, 99)); //빅데이터기술특론2
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01406"), 데이터테크놀로지전공, 0, 16, 99)); //클라우드시스템
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01407"), 데이터테크놀로지전공, 0, 16, 99)); //딥러닝
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01408"), 데이터테크놀로지전공, 0, 16, 99)); //데이터사이언스
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01409"), 데이터테크놀로지전공, 0, 16, 99)); //현장실무교육
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01411"), 데이터테크놀로지전공, 0, 16, 99)); //인터랙션디자인
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HED01410"), 데이터테크놀로지전공, 0, 16,
			99)); //빅데이터시스템특론(대학원)
		lectureSet.add(MajorLecture.of(mockLectureMap.get("HED01412"), 데이터테크놀로지전공, 0, 16,
			99)); //대용량데이터베이스특론(대학원)
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01414"), 데이터테크놀로지전공, 0, 16, 99)); //빅데이터컴퓨팅(대학원)
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01415"), 데이터테크놀로지전공, 0, 16, 99)); //논문연구(대학원)
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HED01416"), 데이터테크놀로지전공, 0, 16, 99)); //고급딥러닝(대학원)
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HEB01101"), 데이터테크놀로지전공, 0, 16, 99)); //절차적사고와프로그래밍
		lectureSet.add(
			MajorLecture.of(mockLectureMap.get("HEB01103"), 데이터테크놀로지전공, 0, 16, 99)); //객체지향적사고와프로그래밍

		return lectureSet;
	}
}
