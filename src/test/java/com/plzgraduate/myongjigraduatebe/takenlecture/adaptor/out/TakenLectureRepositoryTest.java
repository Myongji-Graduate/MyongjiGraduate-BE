package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserRepository;

class TakenLectureRepositoryTest extends PersistenceTestSupport {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LectureRepository lectureRepository;
	@Autowired
	private TakenLectureRepository takenLectureRepository;

	@DisplayName("유저의 수강 과목을 조회한다.")
	@Test
	void findByUser() {
	    //given
		UserJpaEntity user = UserJpaEntity.builder()
			.authId("abc")
			.password("12345153")
			.studentNumber("60191656").build();
		userRepository.save(user);

		LectureJpaEntity lecture = LectureJpaEntity.builder().build();
		lectureRepository.save(lecture);

		TakenLectureJpaEntity takenLectureA = TakenLectureJpaEntity.builder()
			.user(user)
			.lecture(lecture)
			.build();
		TakenLectureJpaEntity takenLectureB = TakenLectureJpaEntity.builder()
			.user(user)
			.lecture(lecture)
			.build();
		takenLectureRepository.saveAll(List.of(takenLectureA, takenLectureB));

		//when
		List<TakenLectureJpaEntity> takenLectures = takenLectureRepository.findByUser(user);

		//then
		assertThat(takenLectures).hasSize(2);
	}

}
