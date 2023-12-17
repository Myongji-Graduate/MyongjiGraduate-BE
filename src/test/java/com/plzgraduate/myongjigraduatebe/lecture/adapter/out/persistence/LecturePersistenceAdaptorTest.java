package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureQueryRepository;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

class LecturePersistenceAdaptorTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private LectureQueryRepository lectureQueryRepository;

	@Autowired
	private LecturePersistenceAdaptor lecturePersistenceAdaptor;

	@AfterEach
	void afterEach() {
		this.lectureRepository.deleteAllInBatch();
		this.entityManager
			.createNativeQuery("ALTER TABLE lecture auto_increment 1")
			.executeUpdate();
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
		List<Lecture> lectures = lecturePersistenceAdaptor.findLecturesByLectureCodes(lectureCodes);

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
		List<Long> lectureIds = new ArrayList<>(List.of(1L, 2L));

		lectureRepository.saveAll(List.of(
			createLectureJpaEntity("code1", "name1"),
			createLectureJpaEntity("code2", "name2"),
			createLectureJpaEntity("code3", "name3"),
			createLectureJpaEntity("code4", "name4")
		));

		//when
		List<Lecture> lectures = lecturePersistenceAdaptor.findLecturesByIds(lectureIds);

		//then
		assertThat(lectures)
			.hasSize(2)
			.extracting("id", "lectureCode", "name")
			.containsExactlyInAnyOrder(
				tuple(1L, "code1", "name1"),
				tuple(2L, "code2", "name2")
			);
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
		List<Lecture> lectures = lecturePersistenceAdaptor.searchLectureByNameOrCode("name", "name");

		//then
		assertThat(lectures)
			.hasSize(2)
			.extracting("lectureCode", "name")
			.containsExactlyInAnyOrder(
				tuple("code1", "name1"),
				tuple("code2", "name2")
			);
	}

	private Lecture createLecture(String lectureCode, String name, int credit, int isRevoked, String duplicateCode) {
		return Lecture.builder()
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.duplicateCode(duplicateCode)
			.build();
	}

	private LectureJpaEntity createLectureJpaEntity(String lectureCode, String name) {
		return LectureJpaEntity.builder()
			.lectureCode(lectureCode)
			.name(name)
			.build();
	}

}
