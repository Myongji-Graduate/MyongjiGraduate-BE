package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureQueryRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LecturePersistenceAdapterTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private LectureQueryRepository lectureQueryRepository;

	@Autowired
	private LecturePersistenceAdapter lecturePersistenceAdapter;

	@AfterEach
	void afterEach() {
		this.lectureRepository.deleteAllInBatch();
	}

	@DisplayName("과목코드 리스트에 속해있는 과목들을 모두 찾는다.")
	@Test
	void findLecturesByLectureCodes() {
		//given
		List<String> lectureCodes = new ArrayList<>(List.of("code1", "code3"));
		lectureRepository.saveAll(List.of(
			createLectureJpaEntity("code1", "name1"),
			createLectureJpaEntity("code2", "name2"),
			createLectureJpaEntity("code3", "name3"),
			createLectureJpaEntity("code4", "name4")
		));

		//when
		List<Lecture> lectures = lecturePersistenceAdapter.findLecturesByLectureCodes(lectureCodes);

		//then
		assertThat(lectures)
			.hasSize(2)
			.extracting("lectureCode", "name")
			.containsExactlyInAnyOrder(
				tuple("code1", "name1"),
				tuple("code3", "name3")
			);
	}

	@DisplayName("아이디 리스트에 속해있는 과목들을 모두 찾는다.")
	@Test
	void findLecturesByIds() {
		//given
		List<String> lectureIds = new ArrayList<>(List.of("code1", "code2"));

		lectureRepository.saveAll(List.of(
			createLectureJpaEntity("code1", "name1"),
			createLectureJpaEntity("code2", "name2"),
			createLectureJpaEntity("code3", "name3"),
			createLectureJpaEntity("code4", "name4")
		));

		//when
		List<Lecture> lectures = lecturePersistenceAdapter.findLecturesByIds(lectureIds);

		//then
		assertThat(lectures)
			.hasSize(2)
			.extracting("id", "name")
			.containsExactlyInAnyOrder(
				tuple("code1", "name1"),
				tuple("code2", "name2")
			);
	}

	@DisplayName("아이디로 과목을 찾는다.")
	@Test
	void findLectureById() {
		//given
		String lectureCode = "code";
		String lectureName = "name";
		LectureJpaEntity lectureJpaEntity = lectureRepository.save(
			createLectureJpaEntity(lectureCode, lectureName));

		//when
		Lecture lecture = lecturePersistenceAdapter.findLectureById(lectureJpaEntity.getId());

		//then
		String expectedLectureId = lectureJpaEntity.getId();
		String expectedLectureName = "name";
		assertThat(lecture.getId()).isEqualTo(expectedLectureId);
		assertThat(lecture.getName()).isEqualTo(expectedLectureName);
	}

	@DisplayName("과목명이나 과목코드를 포함하는 과목들을 찾는다.")
	@Test
	void searchLectureByNameOrCode() {
		//given
		lectureRepository.saveAll(List.of(
			createLectureJpaEntity("code1", "name1"),
			createLectureJpaEntity("code2", "name2")
		));

		//when
		List<Lecture> lectures = lecturePersistenceAdapter.searchLectureByNameOrCode("name",
			"name");

		//then
		assertThat(lectures)
			.hasSize(2)
			.extracting("lectureCode", "name")
			.containsExactlyInAnyOrder(
				tuple("code1", "name1"),
				tuple("code2", "name2")
			);
	}

	private LectureJpaEntity createLectureJpaEntity(String lectureCode, String name) {
		return LectureJpaEntity.builder()
			.id(lectureCode)
			.name(name)
			.build();
	}

}
