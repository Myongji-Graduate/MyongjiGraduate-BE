package com.plzgraduate.myongjigraduatebe.takenlecture.adaptor.out;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.LoadTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.out.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.adaptor.out.persistence.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
class TakenLecturePersistenceAdaptor implements LoadTakenLecturePort, SaveTakenLecturePort, DeleteTakenLecturePort {

	private final TakenLectureRepository takenLectureRepository;
	private final TakenLectureMapper takenLectureMapper;

	@Override
	public List<TakenLecture> loadTakenLecturesByUser(User user) {
		UserJpaEntity userJpaEntity = takenLectureMapper.mapToUserJpaEntity(user);
		List<TakenLectureJpaEntity> takenLectures =
			takenLectureRepository.findTakenLectureJpaEntityWithLectureByUser(userJpaEntity);
		return takenLectures.stream().map(takenLectureMapper::mapToDomainEntity).collect(Collectors.toList());

	}
	@Override
	public void saveTakenLectures(List<TakenLecture> takenLectures) {
		List<TakenLectureJpaEntity> takenLecturesJpaEntities = takenLectures.stream()
			.map(takenLectureMapper::mapToJpaEntity)
			.collect(Collectors.toList());
		takenLectureRepository.saveAll(takenLecturesJpaEntities);
	}

	@Override
	public void deleteAllTakenLecturesByUser(User user) {
		takenLectureRepository.deleteAllByUser(takenLectureMapper.mapToUserJpaEntity(user));
	}

	@Override
	public void deleteTakenLecturesByIds(List<Long> deleteIds) {
		takenLectureRepository.deleteAllByIdInBatch(deleteIds);
	}
}
