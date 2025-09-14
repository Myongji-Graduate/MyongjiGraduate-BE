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
import java.util.LinkedHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
	public List<String> findTakenLectureIdsByUserAndCodes(Long userId, Collection<String> codes) {
		// 1) 입력 가드
		if (userId == null || codes == null || codes.isEmpty()) {
			return List.of();
		}

		// 2) 코드 정규화(공백 제거, 빈 문자열 제거, 중복 제거)
		Set<String> base = codes.stream()
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toCollection(LinkedHashSet::new)); // 순서 보존

		if (base.isEmpty()) {
			return List.of();
		}

		// 3) Repository 위임 (경량 JPQL: select t.lecture.id ...)
		List<String> taken = takenLectureRepository.findTakenLectureIdsByUserAndCodes(userId, base);

		// 4) 혹시 모를 중복 제거 후 반환 (순서 보존)
		return taken.stream()
				.filter(Objects::nonNull)
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.distinct()
				.collect(Collectors.toList());
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
