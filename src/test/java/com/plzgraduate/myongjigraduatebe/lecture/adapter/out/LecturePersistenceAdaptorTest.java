package com.plzgraduate.myongjigraduatebe.lecture.adapter.out;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;

@Import({LectureMapper.class, LecturePersistenceAdaptor.class, TestQuerydslConfig.class})
class LecturePersistenceAdaptorTest extends PersistenceTestSupport {

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private LectureQueryRepository lectureQueryRepository;

	@Autowired
	private LecturePersistenceAdaptor lecturePersistenceAdaptor;

	@DisplayName("과목코드 리스트에 속해있는 과목들을 모두 찾는다.")
	@Test
	void findLecturesByLectureCodes() {
		//given
		List<String> lectureCodes = new ArrayList<>();
		lectureRepository.saveAll(List.of(

		));

		//when
		List<Lecture> lectures = lecturePersistenceAdaptor.findLecturesByLectureCodes(lectureCodes);

		//then
		assertThat(lectures).hasSameSizeAs(lectureCodes);

	}

	@DisplayName("아이디 리스트에 속해있는 과목들을 모두 찾는다.")
	@Test
	void findLecturesByIds() {
		//given
		List<Long> lectureIds = new ArrayList<>();

		lectureRepository.saveAll(List.of(

		));
		//when
		List<Lecture> lectures = lecturePersistenceAdaptor.findLecturesByIds(lectureIds);

		//then
		assertThat(lectures).hasSameSizeAs(lectureIds);
	}

	private Lecture createLecture(Long id, String lectureCode, String name, int credit, int isRevoked, String duplicateCode) {
		return Lecture.builder()
			.id(id)
			.lectureCode(lectureCode)
			.name(name)
			.credit(credit)
			.isRevoked(isRevoked)
			.duplicateCode(duplicateCode)
			.build();
	}

}
