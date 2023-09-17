package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.plzgraduate.myongjigraduatebe.fixture.LectureFixture;
import com.plzgraduate.myongjigraduatebe.fixture.MajorFixture;
import com.plzgraduate.myongjigraduatebe.fixture.UserFixture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

@DisplayName("데이터테크놀로지학과의 전공 달성 여부를 계산한다.")
class DataTechnologyMajorTest {
	private static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("(16학번 해당학번 전공필수과목 적용 확인) 전공 필수과목을 다 듣고, 전공 기준 학점을 넘었을 경우 전공 카테고리를 충족한다.")
	@Test
	void 전공필수_기준학점_충족 () {
    
		//given
		User user = UserFixture.데이테크놀로지학과_16학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("HEB01102"), 2016, Semester.FIRST), //기초프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HEB01103"), 2016, Semester.SECOND), //객체지향적사고와프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HED01201"), 2017, Semester.FIRST), //자료구조
			TakenLecture.of(user, mockLectureMap.get("HED01202"), 2017, Semester.FIRST), //R통계분석
			TakenLecture.of(user, mockLectureMap.get("HEB01105"), 2019, Semester.SECOND), //기초프로그래밍2

			TakenLecture.of(user, mockLectureMap.get("HEB01202"), 2019, Semester.SECOND), //기초웹프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HED01203"), 2019, Semester.SECOND), //데이터베이스
			TakenLecture.of(user, mockLectureMap.get("HED01301"), 2020, Semester.FIRST), //소프트웨어공학
			TakenLecture.of(user, mockLectureMap.get("HED01315"), 2020, Semester.FIRST), //인공지능
			TakenLecture.of(user, mockLectureMap.get("HED01313"), 2020, Semester.FIRST), //컴퓨터통신

			TakenLecture.of(user, mockLectureMap.get("HED01309"), 2020, Semester.SECOND), //컴퓨터아키텍처
			TakenLecture.of(user, mockLectureMap.get("HED01307"), 2020, Semester.SECOND), //알고리즘
			TakenLecture.of(user, mockLectureMap.get("HED01303"), 2020, Semester.SECOND), //운영체제
			TakenLecture.of(user, mockLectureMap.get("HED01306"), 2020, Semester.SECOND), //빅데이터프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HED01308"), 2021, Semester.SECOND), //UX디자인

			TakenLecture.of(user, mockLectureMap.get("HED01403"), 2020, Semester.SECOND), //블록체인기초
			TakenLecture.of(user, mockLectureMap.get("HED01318"), 2021, Semester.FIRST), //모바일컴퓨팅
			TakenLecture.of(user, mockLectureMap.get("HED01317"), 2021, Semester.FIRST), //데이터베이스프로젝트
			TakenLecture.of(user, mockLectureMap.get("HED01401"), 2021, Semester.FIRST), //캡스톤디자인1
			TakenLecture.of(user, mockLectureMap.get("HED01404"), 2021, Semester.FIRST), //빅데이터기술특론1

			TakenLecture.of(user, mockLectureMap.get("HED01407"), 2021, Semester.FIRST), //딥러닝
			TakenLecture.of(user, mockLectureMap.get("HED01405"), 2021, Semester.SECOND), //빅데이터기술특론2
			TakenLecture.of(user, mockLectureMap.get("HED01406"), 2021, Semester.SECOND), //클라우드컴퓨팅
			TakenLecture.of(user, mockLectureMap.get("HED01311"), 2021, Semester.SECOND) //자기주도학

		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		Set<MajorLecture> 데이터테크놀로지_전공 = MajorFixture.데이터테크놀로지_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 데이터테크놀로지_전공, 70);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult mandatoryDetailCategory = detailCategory.get(0);
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then
		assertThat(detailGraduationResult)
			.extracting("isCompleted", "totalCredit", "takenCredit")
			.contains(true, 70, 71);
		assertThat(mandatoryDetailCategory)
			.extracting("isCompleted", "isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(true, true, 36, 36);
		assertThat(mandatoryDetailCategory.getTakenLectures()).hasSize(12);
		assertThat(mandatoryDetailCategory.getHaveToLectures()).isEmpty();
		assertThat(electiveDetailCategory)
			.extracting("isCompleted", "totalCredits", "takenCredits")
			.contains(true, 34, 35);
		assertThat(electiveDetailCategory.getTakenLectures()).hasSize(12);
		assertThat(electiveDetailCategory.getHaveToLectures()).isEmpty();
	}

	@DisplayName("(18학번) 전공 필수과목을 다 듣지 않고, 전공 기준 학점을 넘지 못했을 경우 경우 전공 카테고리를 충족하지 못한다.")
	@Test
	void 전공필수_기준학점_미충족 () {
    
		//given
		User user = UserFixture.데이테크놀로지학과_18학번();
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("HEB01102"), 2018, Semester.FIRST), //기초프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HEB01104"), 2018, Semester.SECOND), //객체지향프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HED01201"), 2019, Semester.FIRST), //자료구조
			TakenLecture.of(user, mockLectureMap.get("HED01202"), 2019, Semester.FIRST), //R통계분석

			TakenLecture.of(user, mockLectureMap.get("HEB01202"), 2019, Semester.SECOND), //기초웹프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HED01203"), 2019, Semester.SECOND), //데이터베이스
			TakenLecture.of(user, mockLectureMap.get("HED01301"), 2020, Semester.FIRST), //소프트웨어공학
			TakenLecture.of(user, mockLectureMap.get("HED01315"), 2020, Semester.FIRST), //인공지능
			TakenLecture.of(user, mockLectureMap.get("HED01313"), 2020, Semester.FIRST), //컴퓨터통신

			TakenLecture.of(user, mockLectureMap.get("HED01314"), 2020, Semester.SECOND), //게임프로그래밍
			TakenLecture.of(user, mockLectureMap.get("HED01309"), 2020, Semester.SECOND), //컴퓨터아키텍처
			TakenLecture.of(user, mockLectureMap.get("HED01307"), 2020, Semester.SECOND), //알고리즘
			TakenLecture.of(user, mockLectureMap.get("HED01303"), 2020, Semester.SECOND), //운영체제
			TakenLecture.of(user, mockLectureMap.get("HED01306"), 2020, Semester.SECOND), //빅데이터프로그래밍

			TakenLecture.of(user, mockLectureMap.get("HED01403"), 2020, Semester.SECOND), //블록체인기초
			TakenLecture.of(user, mockLectureMap.get("HED01318"), 2023, Semester.FIRST), //모바일컴퓨팅
			TakenLecture.of(user, mockLectureMap.get("HED01317"), 2023, Semester.FIRST), //데이터베이스프로젝트
			TakenLecture.of(user, mockLectureMap.get("HED01404"), 2023, Semester.FIRST), //빅데이터프로그래밍1
			TakenLecture.of(user, mockLectureMap.get("HED01407"), 2023, Semester.FIRST) //딥러닝
		)));
		TakenLectureInventory takenLectureInventory = TakenLectureInventory.from(takenLectures);
		Set<MajorLecture> 데이터테크놀로지_전공 = MajorFixture.데이터테크놀로지_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 데이터테크놀로지_전공, 70);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult mandatoryDetailCategory = detailCategory.get(0);
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then
		assertThat(detailGraduationResult)
			.extracting("isCompleted", "totalCredit", "takenCredit")
			.contains(false, 70, 57);
		assertThat(mandatoryDetailCategory)
			.extracting("isCompleted", "isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(false, false, 33, 30);
		assertThat(mandatoryDetailCategory.getTakenLectures()).hasSize(10);
		assertThat(mandatoryDetailCategory.getHaveToLectures()).contains(mockLectureMap.get("HED01413")); //캡스톤을 포함
		assertThat(electiveDetailCategory)
			.extracting("isCompleted", "totalCredits", "takenCredits")
			.contains(false, 37, 27);
		assertThat(electiveDetailCategory.getTakenLectures()).hasSize(9);
		assertThat(electiveDetailCategory.getHaveToLectures()).contains(mockLectureMap.get("HED01308")); //UX디자인을 포함
	}
}
