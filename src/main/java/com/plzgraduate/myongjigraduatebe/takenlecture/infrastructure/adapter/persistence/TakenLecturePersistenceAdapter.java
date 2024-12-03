package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.mapper.TakenLectureMapper;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class TakenLecturePersistenceAdapter implements FindTakenLecturePort, SaveTakenLecturePort,
	DeleteTakenLecturePort {

	private final TakenLectureRepository takenLectureRepository;
	private final TakenLectureMapper takenLectureMapper;

	@Override
	public List<TakenLecture> findTakenLecturesByUser(User user) {
		UserJpaEntity userJpaEntity = takenLectureMapper.mapToUserJpaEntity(user);
		List<TakenLectureJpaEntity> takenLectures =
			takenLectureRepository.findTakenLectureJpaEntityWithLectureByUser(userJpaEntity);
		return takenLectures.stream()
			.map(takenLectureMapper::mapToDomainEntity)
			.collect(Collectors.toList());

	}

	@Override
	public Set<TakenLecture> findTakenLectureSetByUser(User user) {
		UserJpaEntity userJpaEntity = takenLectureMapper.mapToUserJpaEntity(user);
		List<TakenLectureJpaEntity> takenLectures =
			takenLectureRepository.findTakenLectureJpaEntityWithLectureByUser(userJpaEntity);
		return takenLectures.stream()
			.map(takenLectureMapper::mapToDomainEntity)
			.collect(Collectors.toSet());
	}

	@Override
	public void saveTakenLectures(List<TakenLecture> takenLectures) {
		List<TakenLectureJpaEntity> takenLecturesJpaEntities = takenLectures.stream()
			.map(takenLectureMapper::mapToJpaEntity)
			.collect(Collectors.toList());
		takenLectureRepository.saveAll(takenLecturesJpaEntities);
	}

	@Override
	public void saveTakenLecture(final TakenLecture takenLecture) {
		TakenLectureJpaEntity takenLectureJpaEntity = takenLectureMapper.mapToJpaEntity(
			takenLecture);
		takenLectureRepository.save(takenLectureJpaEntity);
	}

	@Override
	public void deleteAllTakenLecturesByUser(User user) {
		takenLectureRepository.deleteAllByUser(takenLectureMapper.mapToUserJpaEntity(user));
	}

	@Override
	public void deleteTakenLectureById(Long deleteId) {
		takenLectureRepository.deleteById(deleteId);
	}
}
