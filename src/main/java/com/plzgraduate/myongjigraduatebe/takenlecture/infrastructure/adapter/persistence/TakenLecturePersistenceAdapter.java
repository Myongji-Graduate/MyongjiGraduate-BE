package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.DeleteTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.SaveTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.TakenLectureReadContextPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.TakenLectureRevisionPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.mapper.TakenLectureMapper;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.mapper.UserMapper;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.repository.UserRepository;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@PersistenceAdapter
@RequiredArgsConstructor
public class TakenLecturePersistenceAdapter implements FindTakenLecturePort, SaveTakenLecturePort,
    DeleteTakenLecturePort, TakenLectureRevisionPort,
    TakenLectureReadContextPort {

	private final TakenLectureRepository takenLectureRepository;
	private final TakenLectureMapper takenLectureMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ReadContext findReadContext(Long userId) {
        var row = userRepository.findTakenLectureReadContext(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        if (row.catalogRevision() == null || row.catalogRevision() < 0) {
            throw new IllegalStateException("Lecture catalog revision is not initialized; apply V15 before enabling cache");
        }
        return new ReadContext(userMapper.mapToDomainEntity(row.user()), row.revision(), row.catalogRevision());
    }

    @Override
    public long findRevision(Long userId) {
        return userRepository.findTakenLectureRevision(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void lockForChange(Long userId) {
        userRepository.findByIdForUpdate(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
    }

    private void advanceRevision(Long userId) {
        if (userRepository.advanceTakenLectureRevision(userId) != 1) {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
    }

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
    @Transactional(propagation = Propagation.MANDATORY)
	public void saveTakenLectures(List<TakenLecture> takenLectures) {
        takenLectures.stream().map(t -> t.getUser().getId()).distinct().sorted().forEach(this::advanceRevision);
		List<TakenLectureJpaEntity> takenLecturesJpaEntities = takenLectures.stream()
			.map(takenLectureMapper::mapToJpaEntity)
			.collect(Collectors.toList());
		takenLectureRepository.saveAll(takenLecturesJpaEntities);
	}

	@Override
    @Transactional(propagation = Propagation.MANDATORY)
	public void saveTakenLecture(final TakenLecture takenLecture) {
        advanceRevision(takenLecture.getUser().getId());
		TakenLectureJpaEntity takenLectureJpaEntity = takenLectureMapper.mapToJpaEntity(
			takenLecture);
		takenLectureRepository.save(takenLectureJpaEntity);
	}

	@Override
    @Transactional(propagation = Propagation.MANDATORY)
	public void deleteAllTakenLecturesByUser(User user) {
        advanceRevision(user.getId());
		takenLectureRepository.deleteAllByUser(takenLectureMapper.mapToUserJpaEntity(user));
	}

	@Override
    @Transactional(propagation = Propagation.MANDATORY)
	public void deleteTakenLectureById(Long deleteId) {
        var taken = takenLectureRepository.findById(deleteId);
        if (taken.isEmpty()) return;
        if (taken.get().getUser() != null) advanceRevision(taken.get().getUser().getId());
		takenLectureRepository.deleteById(deleteId);
	}

}
