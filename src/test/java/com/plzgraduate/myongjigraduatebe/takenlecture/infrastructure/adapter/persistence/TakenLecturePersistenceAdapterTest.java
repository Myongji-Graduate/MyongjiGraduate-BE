package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.support.PersistenceTestSupport;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TakenLecturePersistenceAdapterTest extends PersistenceTestSupport {

	@Autowired
	private TakenLectureRepository takenLectureRepository;

	@Autowired
	private TakenLecturePersistenceAdapter takenLecturePersistenceAdapter;

	@AfterEach
	void afterEach() {
		this.takenLectureRepository.deleteAllInBatch();
		this.entityManager
			.createNativeQuery("ALTER TABLE lecture auto_increment 1")
			.executeUpdate();
	}

	@DisplayName("수강과목을 삭제합니다.")
	@Test
	public void deleteTakenLectureById() throws Exception {
		// given
		TakenLectureJpaEntity takenLectureJpaEntity = createTakenLectureJpaEntity(2099,
			Semester.FIRST);
		takenLectureRepository.save(takenLectureJpaEntity);

		// when
		takenLecturePersistenceAdapter.deleteTakenLectureById(takenLectureJpaEntity.getId());

		// then
		Optional<TakenLectureJpaEntity> result = takenLectureRepository.findById(
			takenLectureJpaEntity.getId());
		assertThat(result.isPresent()).isFalse();
	}

	private TakenLectureJpaEntity createTakenLectureJpaEntity(Integer year, Semester semester) {
		return TakenLectureJpaEntity.builder()
			.year(year)
			.semester(semester)
			.build();
	}
}
