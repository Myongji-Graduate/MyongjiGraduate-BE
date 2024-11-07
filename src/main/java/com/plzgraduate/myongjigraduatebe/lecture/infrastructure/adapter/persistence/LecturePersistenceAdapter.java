package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FindLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.SearchLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureQueryRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.LectureRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class LecturePersistenceAdapter implements FindLecturePort, SearchLecturePort {

	private final static String NOT_FOUND_LECTURE_ERROR_MESSAGE = "해당 과목을 찾을 수 없습니다.";
	private final LectureQueryRepository lectureQueryRepository;
	private final LectureRepository lectureRepository;
	private final LectureMapper lectureMapper;

	@Override
	public List<Lecture> findLecturesByLectureCodes(List<String> lectureCodes) {
		List<LectureJpaEntity> lectureJpaEntities = lectureRepository.findByLectureCodeIn(
			lectureCodes);
		return lectureJpaEntities.stream()
			.map(lectureMapper::mapToLectureModel)
			.collect(Collectors.toList());
	}

	@Override
	public List<Lecture> findLecturesByIds(List<Long> lectureIds) {
		List<LectureJpaEntity> lectureJpaEntities = lectureRepository.findByIdIn(lectureIds);
		return lectureJpaEntities.stream()
			.map(lectureMapper::mapToLectureModel)
			.collect(Collectors.toList());
	}

	@Override
	public Lecture findLectureById(final Long lectureId) {
		LectureJpaEntity lectureJpaEntity = lectureRepository.findById(lectureId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_LECTURE_ERROR_MESSAGE));
		return lectureMapper.mapToLectureModel(lectureJpaEntity);
	}

	@Override
	public List<Lecture> searchLectureByNameOrCode(String type, String keyword) {
		List<LectureJpaEntity> lectureJpaEntities = lectureQueryRepository.searchByNameOrCode(type,
			keyword);
		return lectureJpaEntities.stream()
			.map(lectureMapper::mapToLectureModel)
			.collect(Collectors.toList());
	}
}
