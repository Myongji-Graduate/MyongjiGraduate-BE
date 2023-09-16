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

@DisplayName("국제통상학과의 전공 달성여부를 계산한다.")
class InternationTradeMajorTest {
	private static final User user = UserFixture.국제통상학과_19학번();
	private static final Map<String, Lecture> mockLectureMap = LectureFixture.getMockLectureMap();

	@DisplayName("전공 필수과목을 다 듣고, 전공 기준 학점을 넘겼을 경우 전공 카테고리를 충족한다.")
	@Test
	void 전공필수_기준학점_충족() {
    
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			//전공필수 -> 21학점
			TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019, Semester.FIRST), //국제통상원론
			TakenLecture.of(user, mockLectureMap.get("HBX01129"), 2019, Semester.FIRST), //글로벌경영전략
			TakenLecture.of(user, mockLectureMap.get("HBX01127"), 2021, Semester.FIRST), //국제경영학
			TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2019, Semester.SECOND), //회계원리
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.SECOND), //인적자원관리
			TakenLecture.of(user, mockLectureMap.get("HBX01106"), 2020, Semester.FIRST), //마켓팅원론
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			//전공선택 -> 42학점
			TakenLecture.of(user, mockLectureMap.get("HCC04432"), 2020, Semester.SECOND), //해상보험론
			TakenLecture.of(user, mockLectureMap.get("HBX01142"), 2021, Semester.SECOND), //창업과경영캡스톤
			TakenLecture.of(user, mockLectureMap.get("HBW01202"), 2021, Semester.FIRST), //국제금융론
			TakenLecture.of(user, mockLectureMap.get("HBW01203"), 2021, Semester.FIRST), //파생금융상품론
			TakenLecture.of(user, mockLectureMap.get("HBW01401"), 2022, Semester.FIRST), //외환론
			TakenLecture.of(user, mockLectureMap.get("HBV01301"), 2022, Semester.FIRST), //전자상거래
			TakenLecture.of(user, mockLectureMap.get("HCC04220"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04236"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04350"), 2021, Semester.FIRST), //EU통상론
			TakenLecture.of(user, mockLectureMap.get("HCC04312"), 2022, Semester.SECOND), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04456"), 2022, Semester.SECOND), //미국경제론
			TakenLecture.of(user, mockLectureMap.get("HCC04461"), 2022, Semester.SECOND), //국제통상세미나
			TakenLecture.of(user, mockLectureMap.get("HCC04343"), 2022, Semester.SECOND), //해외시장조사론
			TakenLecture.of(user, mockLectureMap.get("HCC04496"), 2022, Semester.SECOND) //글로벌전략계획
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		Set<MajorLecture> 국제통상_전공 = MajorFixture.국제통상_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 국제통상_전공, 63);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult mandatoryDetailCategory = detailCategory.get(0);
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then
		assertThat(detailGraduationResult)
			.extracting("isCompleted", "totalCredit", "takenCredit")
			.contains(true, 63, 63);
		assertThat(mandatoryDetailCategory)
			.extracting("isCompleted", "isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(true, true, 21, 21);
		assertThat(mandatoryDetailCategory.getTakenLectures()).hasSize(7);
		assertThat(mandatoryDetailCategory.getHaveToLectures()).isEmpty();
		assertThat(electiveDetailCategory)
			.extracting("isCompleted", "totalCredits", "takenCredits")
			.contains(true, 42, 42);
		assertThat(electiveDetailCategory.getTakenLectures()).hasSize(14);
		assertThat(electiveDetailCategory.getHaveToLectures()).isEmpty();
	}

	@DisplayName("전공 기준학점을 넘겼으나 전공 필수를 다 듣지 않은 경우 충족하지 못한다.")
	@Test
	void 기준학점_충족_전공필수_미충족() {
    
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019, Semester.FIRST), //국제통상원론
			TakenLecture.of(user, mockLectureMap.get("HBX01129"), 2021, Semester.FIRST), //글로벌경영전략
			TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2021, Semester.SECOND), //회계원리
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.SECOND), //인적자원관리
			TakenLecture.of(user, mockLectureMap.get("HBX01106"), 2020, Semester.FIRST), //마켓팅원론
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			TakenLecture.of(user, mockLectureMap.get("HBX01143"), 2020, Semester.SECOND), //운영관리

			TakenLecture.of(user, mockLectureMap.get("HCC04432"), 2020, Semester.SECOND), //해상보험론
			TakenLecture.of(user, mockLectureMap.get("HBX01142"), 2021, Semester.SECOND), //창업과경영캡스톤
			TakenLecture.of(user, mockLectureMap.get("HBW01202"), 2021, Semester.FIRST), //국제금융론
			TakenLecture.of(user, mockLectureMap.get("HBW01203"), 2021, Semester.FIRST), //파생금융상품론
			TakenLecture.of(user, mockLectureMap.get("HBW01401"), 2022, Semester.FIRST), //외환론
			TakenLecture.of(user, mockLectureMap.get("HBV01301"), 2022, Semester.FIRST), //전자상거래
			TakenLecture.of(user, mockLectureMap.get("HCC04220"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04236"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04350"), 2021, Semester.FIRST), //EU통상론
			TakenLecture.of(user, mockLectureMap.get("HCC04312"), 2022, Semester.SECOND), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04456"), 2022, Semester.SECOND), //미국경제론
			TakenLecture.of(user, mockLectureMap.get("HCC04461"), 2022, Semester.SECOND), //국제통상세미나
			TakenLecture.of(user, mockLectureMap.get("HCC04343"), 2022, Semester.SECOND), //해외시장조사론
			TakenLecture.of(user, mockLectureMap.get("HCC04496"), 2022, Semester.SECOND) //글로벌전략계획
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		Set<MajorLecture> 국제통상_전공 = MajorFixture.국제통상_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 국제통상_전공, 63);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult mandatoryDetailCategory = detailCategory.get(0);
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then

		assertThat(detailGraduationResult)
			.extracting("isCompleted", "totalCredit", "takenCredit")
			.contains(false, 63, 63);
		assertThat(mandatoryDetailCategory)
			.extracting("isCompleted", "isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(false, true, 21, 18);
		assertThat(mandatoryDetailCategory.getTakenLectures()).hasSize(6);
		assertThat(mandatoryDetailCategory.getHaveToLectures()).hasSize(1);
		assertThat(electiveDetailCategory)
			.extracting("isCompleted", "totalCredits", "takenCredits")
			.contains(true, 42, 45);
		assertThat(electiveDetailCategory.getTakenLectures()).hasSize(15);
		assertThat(electiveDetailCategory.getHaveToLectures()).isEmpty();
	}

	@DisplayName("전공선택필수를 다 듣지 않은 경우 충족하지 못한다.")
	@Test
	void 전공선택필수_미충족() {
    
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019, Semester.FIRST), //국제통상원론
			TakenLecture.of(user, mockLectureMap.get("HBX01129"), 2021, Semester.FIRST), //글로벌경영전략
			TakenLecture.of(user, mockLectureMap.get("HBX01127"), 2021, Semester.FIRST), //국제경영학
			TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2021, Semester.SECOND), //회계원리
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.SECOND), //인적자원관리
			TakenLecture.of(user, mockLectureMap.get("HBX01114"), 2020, Semester.SECOND), //생산운영관리

			TakenLecture.of(user, mockLectureMap.get("HCC04432"), 2020, Semester.SECOND), //해상보험론
			TakenLecture.of(user, mockLectureMap.get("HBX01142"), 2021, Semester.SECOND), //창업과경영캡스톤
			TakenLecture.of(user, mockLectureMap.get("HBW01202"), 2021, Semester.FIRST), //국제금융론
			TakenLecture.of(user, mockLectureMap.get("HBW01203"), 2021, Semester.FIRST), //파생금융상품론
			TakenLecture.of(user, mockLectureMap.get("HBW01401"), 2022, Semester.FIRST), //외환론
			TakenLecture.of(user, mockLectureMap.get("HBV01301"), 2022, Semester.FIRST), //전자상거래
			TakenLecture.of(user, mockLectureMap.get("HCC04220"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04236"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04350"), 2021, Semester.FIRST), //EU통상론
			TakenLecture.of(user, mockLectureMap.get("HCC04312"), 2022, Semester.SECOND), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04456"), 2022, Semester.SECOND), //미국경제론
			TakenLecture.of(user, mockLectureMap.get("HCC04461"), 2022, Semester.SECOND), //국제통상세미나
			TakenLecture.of(user, mockLectureMap.get("HCC04343"), 2022, Semester.SECOND), //해외시장조사론
			TakenLecture.of(user, mockLectureMap.get("HCC04496"), 2022, Semester.SECOND), //글로벌전략계획
			TakenLecture.of(user, mockLectureMap.get("HCC04426"), 2023, Semester.SECOND) //다국적기업론
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		Set<MajorLecture> 국제통상_전공 = MajorFixture.국제통상_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 국제통상_전공, 63);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult mandatoryDetailCategory = detailCategory.get(0);
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then
		assertThat(detailGraduationResult)
			.extracting("isCompleted", "totalCredit", "takenCredit")
			.contains(false, 63, 63);
		assertThat(mandatoryDetailCategory)
			.extracting("isCompleted", "isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(false, false, 21, 18);
		assertThat(mandatoryDetailCategory.getTakenLectures()).hasSize(6);
		assertThat(mandatoryDetailCategory.getHaveToLectures()).hasSize(2);
		assertThat(electiveDetailCategory)
			.extracting("isCompleted", "totalCredits", "takenCredits")
			.contains(true, 42, 45);
		assertThat(electiveDetailCategory.getTakenLectures()).hasSize(15);
		assertThat(electiveDetailCategory.getHaveToLectures()).isEmpty();
	}

	@DisplayName("전공선택학점을 다 채우지 못한 경우 전공 카테고리를 충족 못한다.")
	@Test
	void 전공선택_기준학점_미충족() {
    
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			//전공필수
			TakenLecture.of(user, mockLectureMap.get("HBX01128"), 2019, Semester.FIRST), //국제통상원론
			TakenLecture.of(user, mockLectureMap.get("HBX01129"), 2019, Semester.FIRST), //글로벌경영전략
			TakenLecture.of(user, mockLectureMap.get("HBX01127"), 2021, Semester.FIRST), //국제경영학
			TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2019, Semester.SECOND), //회계원리
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.SECOND), //인적자원관리
			TakenLecture.of(user, mockLectureMap.get("HBX01106"), 2020, Semester.FIRST), //마켓팅원론
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			//전공선택
			TakenLecture.of(user, mockLectureMap.get("HCC04432"), 2020, Semester.SECOND), //해상보험론
			TakenLecture.of(user, mockLectureMap.get("HBX01142"), 2021, Semester.SECOND), //창업과경영캡스톤
			TakenLecture.of(user, mockLectureMap.get("HBW01202"), 2021, Semester.FIRST), //국제금융론
			TakenLecture.of(user, mockLectureMap.get("HBW01203"), 2021, Semester.FIRST), //파생금융상품론
			TakenLecture.of(user, mockLectureMap.get("HBW01401"), 2022, Semester.FIRST), //외환론
			TakenLecture.of(user, mockLectureMap.get("HBV01301"), 2022, Semester.FIRST), //전자상거래
			TakenLecture.of(user, mockLectureMap.get("HCC04220"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04236"), 2022, Semester.FIRST), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04312"), 2022, Semester.SECOND), //국제통상법
			TakenLecture.of(user, mockLectureMap.get("HCC04456"), 2022, Semester.SECOND), //미국경제론
			TakenLecture.of(user, mockLectureMap.get("HCC04461"), 2022, Semester.SECOND) //국제통상세미나
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		Set<MajorLecture> 국제통상_전공 = MajorFixture.국제통상_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 국제통상_전공, 63);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult mandatoryDetailCategory = detailCategory.get(0);
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then
		assertThat(detailGraduationResult)
			.extracting("isCompleted", "totalCredit", "takenCredit")
			.contains(false, 63, 54);
		assertThat(mandatoryDetailCategory)
			.extracting("isCompleted", "isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(true, true, 21, 21);
		assertThat(mandatoryDetailCategory.getTakenLectures()).hasSize(7);
		assertThat(mandatoryDetailCategory.getHaveToLectures()).isEmpty();
		assertThat(electiveDetailCategory)
			.extracting("isCompleted", "totalCredits", "takenCredits")
			.contains(false, 42, 33);
		assertThat(electiveDetailCategory.getTakenLectures()).hasSize(11);
		assertThat(electiveDetailCategory.getHaveToLectures()).hasSize(21);
	}

	@DisplayName("전공선택필수 과목을 만족하고 추가로 들었을 경우 전공선택으로 인정된다.")
	@Test
	void 전공필수_추가과목_전공선택인정() {
    
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2019, Semester.FIRST), //회계원리
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.FIRST), //인적자원관리
			TakenLecture.of(user, mockLectureMap.get("HBX01106"), 2020, Semester.FIRST), //마케팅원론
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원론
			TakenLecture.of(user, mockLectureMap.get("HBX01143"), 2021, Semester.FIRST) //운영관리
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		Set<MajorLecture> 국제통상_전공 = MajorFixture.국제통상_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 국제통상_전공, 70);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult mandatoryDetailCategory = detailCategory.get(0);
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then
		assertThat(mandatoryDetailCategory)
			.extracting("isSatisfiedMandatory", "totalCredits", "takenCredits")
			.contains(true, 21, 12);
		assertThat(mandatoryDetailCategory.getTakenLectures()).hasSize(4);
		assertThat(mandatoryDetailCategory.getHaveToLectures()).hasSize(3);
		assertThat(electiveDetailCategory.getTakenLectures()).hasSize(1);
	}

	@DisplayName("전공선택필수에서 수강한 과목에 중복과목이 추천과목에 뜨지 않아야한다.")
	@Test
	void 중복과목_추천과목제외() {
    
		//given
		Set<TakenLecture> takenLectures = new HashSet<>((Set.of(
			TakenLecture.of(user, mockLectureMap.get("HBX01104"), 2019, Semester.FIRST), //회계원리
			TakenLecture.of(user, mockLectureMap.get("HBX01113"), 2019, Semester.FIRST), //인적자원관리
			TakenLecture.of(user, mockLectureMap.get("HBX01105"), 2020, Semester.SECOND), //재무관리원리
			TakenLecture.of(user, mockLectureMap.get("HBX01114"), 2021, Semester.FIRST) //생산운영관리
		)));
		TakenLectureInventory takenLectureInventory = new TakenLectureInventory(takenLectures);
		Set<MajorLecture> 국제통상_전공 = MajorFixture.국제통상_전공();
		MajorManager manager = new MajorManager();

		//when
		DetailGraduationResult detailGraduationResult = manager.createDetailGraduationResult(user,
			takenLectureInventory, 국제통상_전공, 70);
		List<DetailCategoryResult> detailCategory = detailGraduationResult.getDetailCategory();
		DetailCategoryResult electiveDetailCategory = detailCategory.get(1);

		//then
		assertThat(electiveDetailCategory.getHaveToLectures())
			.contains(mockLectureMap.get("HBX01106"))
			.doesNotContain(mockLectureMap.get("HBX01143"));
	}

}
