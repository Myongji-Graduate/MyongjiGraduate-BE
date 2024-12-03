package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureQueryRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LectureQueryRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private LectureQueryRepository lectureQueryRepository;

	@DisplayName("type이 code일 때 keyword에 해당한 과목코드를 포함하는 모든 과목들을 찾는다.")
	@Test
	void searchLectureByCode() {
		//given
		List<LectureJpaEntity> lectureJpaEntities = List.of(
			createLectureJpaEntity("HEB01102", "기초프로그래밍"),
			createLectureJpaEntity("HED01315", "인공지능"),
			createLectureJpaEntity("KMI02118", "엑셀기초및실무활용"),
			createLectureJpaEntity("KMI02123", "웹과앱기초")
		);
		lectureRepository.saveAll(lectureJpaEntities);

		//when
		List<LectureJpaEntity> byCode = lectureQueryRepository.searchByNameOrCode("code", "KM");

		//then
		assertThat(byCode)
			.hasSize(2)
			.extracting("id")
			.containsExactlyInAnyOrder("KMI02118", "KMI02123");

	}

	@DisplayName("type이 name일 때 keyword에 해당한 과목코드를 포함하는 모든 과목들을 찾는다.")
	@Test
	void searchLectureByName() {
		//given
		List<LectureJpaEntity> lectureJpaEntities = List.of(
			createLectureJpaEntity("HEB01102", "기초프로그래밍"),
			createLectureJpaEntity("KMI00000", "기프초"),
			createLectureJpaEntity("HED01315", "인공지능"),
			createLectureJpaEntity("KMI02118", "엑셀기초및실무활용"),
			createLectureJpaEntity("KMI02123", "웹과앱기초")
		);
		lectureRepository.saveAll(lectureJpaEntities);

		//when
		List<LectureJpaEntity> byName = lectureQueryRepository.searchByNameOrCode("name", "기초");

		//then
		assertThat(byName)
			.hasSize(3)
			.extracting("name")
			.containsExactlyInAnyOrder("기초프로그래밍", "엑셀기초및실무활용", "웹과앱기초");
	}

	private LectureJpaEntity createLectureJpaEntity(String lectureCode, String name) {
		return LectureJpaEntity.builder()
			.id(lectureCode)
			.name(name)
			.build();
	}

}
