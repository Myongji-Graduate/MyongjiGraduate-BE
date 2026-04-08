package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DetailCategoryResultTest {

	@DisplayName("addRecognizedLectures - 중복 없는 강의가 추가되면 takenCredits가 증가하고 추가된 학점을 반환한다.")
	@Test
	void addRecognizedLectures_nonDuplicate_addsCredits() {
		// given
		DetailCategoryResult result = DetailCategoryResult.create("전공선택", false, 30);
		Lecture lecture1 = Lecture.builder().id("LEC001").credit(3).build();
		Lecture lecture2 = Lecture.builder().id("LEC002").credit(3).build();

		// when
		int added = result.addRecognizedLectures(List.of(lecture1, lecture2));

		// then
		assertThat(added).isEqualTo(6);
		assertThat(result.getTakenCredits()).isEqualTo(6);
		assertThat(result.getTakenLectures()).hasSize(2);
	}

	@DisplayName("addRecognizedLectures - 이미 존재하는 강의를 추가하면 학점이 증가하지 않는다.")
	@Test
	void addRecognizedLectures_duplicate_doesNotAddCredits() {
		// given
		DetailCategoryResult result = DetailCategoryResult.create("전공선택", false, 30);
		Lecture lecture = Lecture.builder().id("LEC001").credit(3).build();
		result.addRecognizedLectures(List.of(lecture)); // 먼저 추가

		// when - 동일 강의 다시 추가 (addedLectureIds 가 빈 상태가 되는 경로)
		int added = result.addRecognizedLectures(List.of(lecture));

		// then
		assertThat(added).isEqualTo(0);
		assertThat(result.getTakenCredits()).isEqualTo(3); // 변화 없음
		assertThat(result.getTakenLectures()).hasSize(1); // 중복 추가 안됨
	}

	@DisplayName("addRecognizedLectures - 빈 컬렉션을 추가하면 0이 반환된다.")
	@Test
	void addRecognizedLectures_emptyCollection_returnsZero() {
		// given
		DetailCategoryResult result = DetailCategoryResult.create("전공선택", false, 30);

		// when
		int added = result.addRecognizedLectures(List.of());

		// then
		assertThat(added).isEqualTo(0);
		assertThat(result.getTakenCredits()).isEqualTo(0);
	}

	@DisplayName("addRecognizedLectures - 추가된 강의가 haveToLectures에 있으면 제거된다.")
	@Test
	void addRecognizedLectures_removesFromHaveToLectures() {
		// given
		DetailCategoryResult result = DetailCategoryResult.create("전공필수", true, 6);
		Lecture requiredLecture = Lecture.builder().id("MUST001").credit(3).isRevoked(0).build();

		// haveToLectures에 강의 추가 (graduationLectures로 calculate 호출하여 설정)
		result.calculate(new HashSet<>(), new HashSet<>(Set.of(requiredLecture)));
		assertThat(result.getHaveToLectures()).contains(requiredLecture);

		// when
		result.addRecognizedLectures(List.of(requiredLecture));

		// then - haveToLectures에서 제거됨
		assertThat(result.getHaveToLectures()).doesNotContain(requiredLecture);
	}
}
